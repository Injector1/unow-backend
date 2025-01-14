package com.umbrellanow.unow_backend.modules.rental.domain;

import com.umbrellanow.unow_backend.integrations.paypal.PayPalService;
import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.RentalRepository;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.domain.TransactionService;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.domain.UserService;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.security.utils.AuthenticationUtils;
import com.umbrellanow.unow_backend.shared.enumeration.RentalStatus;
import com.umbrellanow.unow_backend.shared.enumeration.RentalType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final UmbrellaService umbrellaService;
    private final UserService userService;
    private final PayPalService payPalService;
    private final TransactionService transactionService;


    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository,
                             UmbrellaService umbrellaService,
                             UserService userService,
                             PayPalService payPalService,
                             TransactionService transactionService) {
        this.rentalRepository = rentalRepository;
        this.umbrellaService = umbrellaService;
        this.userService = userService;
        this.payPalService = payPalService;
        this.transactionService = transactionService;
    }


    @Override
    public Rental getRentalByID(long rentalID) {
        return rentalRepository.findById(rentalID).orElse(null);
    }

    @Transactional
    @Override
    public String getApprovalURLForUmbrellaDeposit(long umbrellaID) throws IOException {
        Umbrella umbrella = umbrellaService.getUmbrellaByID(umbrellaID);
        User userByEmail = userService.getUserByEmail(AuthenticationUtils.getCurrentUserEmail());

        if (umbrella == null || umbrella.isCurrentlyLeased()) {
            throw new IllegalArgumentException("Umbrella not available.");
        }

        if (userByEmail == null) {
            throw new IllegalArgumentException("User not found");
        }

        Double deposit = umbrella.getUmbrellaGroup().getPriceRate().getDeposit();
        Map<String, String> payPalData = payPalService.createPaymentOrder(deposit);

        transactionService.createDepositTransaction(userByEmail, umbrella, deposit, payPalData.get("orderId"));
        return payPalData.get("approvalLink");
    }

    @Transactional
    @Override
    public String getApprovalURLForFinalPayment(long umbrellaID, String userEmail) throws IOException {
        Umbrella umbrella = umbrellaService.getUmbrellaByID(umbrellaID);
        User userByEmail = userService.getUserByEmail(userEmail);

        if (umbrella == null || !umbrella.isCurrentlyLeased()) {
            throw new IllegalArgumentException("Umbrella not available.");
        }

        if (userByEmail == null) {
            throw new IllegalArgumentException("User not found");
        }

        Rental rental = getRentalByUmbrellaIDAndUserID(umbrella.getId(), userByEmail.getId());
        double totalCost = calculateTotalCost(rental);

        if (totalCost == 0) {
            // TODO: handle properly when cost == 0. Now we are charging minimum amount
            totalCost = getRateForRental(rental);
        }
        rental.setTotalCost(totalCost);

        Rental savedRental = rentalRepository.save(rental);
        Map<String, String> paymentOrder = payPalService.createPaymentOrder(totalCost);
        transactionService.createPaymentTransaction(savedRental, paymentOrder.get("orderId"));
        return paymentOrder.get("approvalLink");
    }

    @Transactional
    @Override
    public StorageBox rentUmbrellaAndGetLockerInfo(String orderID,
                                                   String rentalType,
                                                   long umbrellaID,
                                                   String userEmail) throws IOException {
        String captureId = payPalService.capturePayment(orderID);

        if (captureId != null) {
            Rental rental = addRentalRecord(
                    RentalType.parseType(rentalType),
                    userService.getUserByEmail(userEmail),
                    umbrellaService.getUmbrellaByID(umbrellaID),
                    null
            );
            Transaction transaction = transactionService.updateTransaction(
                    orderID,
                    captureId,
                    rental
            );
            umbrellaService.markUmbrellaAsLeased(umbrellaID);
            return transaction.getAssociatedUmbrella().getStorageBox();
        } else {
            throw new RuntimeException("Payment capture failed.");
        }
    }

    @Transactional
    @Override
    public void returnUmbrellaAndRefundDeposit(String orderID, long umbrellaID, String userEmail) throws IOException {
        String captureId = payPalService.capturePayment(orderID);

        User userByEmail = userService.getUserByEmail(userEmail);

        if (userByEmail == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (captureId != null) {
            Rental rental = getRentalByUmbrellaIDAndUserID(umbrellaID, userByEmail.getId());
            Transaction depositByRental = transactionService.findDepositByRental(rental);
            boolean refundSucceeded =
                    payPalService.refundPayment(depositByRental.getCaptureID(), depositByRental.getAmount());

            if (refundSucceeded) {
                transactionService.createRefundTransaction(rental, depositByRental.getAmount());
                rental.setStatus(RentalStatus.COMPLETED);
                rentalRepository.save(rental);
            } else {
                throw new RuntimeException("Refund failed. Contact support");
            }
        } else {
            throw new RuntimeException("Payment capture failed.");
        }
    }

    @Override
    public Collection<Rental> getAllRentalsForUser(String userEmail) {
        User userByEmail = userService.getUserByEmail(userEmail);

        if (userByEmail == null) {
            throw new IllegalArgumentException("User not found");
        }
        return rentalRepository.findAllByUser_id(userByEmail.getId());
    }

    @Override
    public double calculateRentalCost(Long umbrellaID, String userEmail) {
        if (umbrellaID == null) {
            throw new IllegalArgumentException("Umbrella not found");
        }

        User userByEmail = userService.getUserByEmail(userEmail);
        if (userByEmail == null) {
            throw new IllegalArgumentException("User not found");
        }

        Rental rental = getRentalByUmbrellaIDAndUserID(umbrellaID, userByEmail.getId());
        return calculateTotalCost(rental);
    }

    @Override
    public double getRateForRental(Rental rental) {
        if (rental.getType() == RentalType.DAILY) {
            return rental.getUmbrella().getUmbrellaGroup().getPriceRate().getDailyRate();
        } else {
            return rental.getUmbrella().getUmbrellaGroup().getPriceRate().getHourlyRate();
        }
    }

    @Override
    public Rental addRentalRecord(RentalType rentalType, User user, Umbrella umbrella, Discount discount) {
        Rental rental = new Rental();
        rental.setType(rentalType);
        rental.setUser(user);
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setUmbrella(umbrella);
        rental.setDiscount(discount);
        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> getAllRentalsByUserID(long userID) {
        return rentalRepository.findAllByUser_id(userID);
    }

    @Override
    public List<Rental> getAllRentalsByUmbrellaID(long umbrellaID) {
        return rentalRepository.findAllByUmbrella_id(umbrellaID);
    }


    private Rental getRentalByUmbrellaIDAndUserID(Long umbrellaID, Long userID) {
        List<Rental> rentals = rentalRepository.findAllByUmbrella_idAndUser_idAndStatus(
                umbrellaID,
                userID,
                RentalStatus.ACTIVE
        );
        if (rentals.isEmpty()) {
            throw new IllegalArgumentException("No active rentals found");
        }

        if (rentals.size() > 1) {
            throw new RuntimeException("Too many rentals for user");
        }

        return rentals.getFirst();
    }

    private double calculateTotalCost(Rental rental) {
        return switch (rental.getType()) {
            case DAILY -> rental.getUmbrella().getUmbrellaGroup().getPriceRate().getDailyRate()
                        * Duration.between(rental.getCreatedDate(), LocalDateTime.now()).toDays();
            case HOURLY -> rental.getUmbrella().getUmbrellaGroup().getPriceRate().getHourlyRate()
                        * Duration.between(rental.getCreatedDate(), LocalDateTime.now()).toHours();
            case null -> throw new RuntimeException("No rate specified");
        };
    }
}
