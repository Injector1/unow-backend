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
        Map<String, String> payPalData = payPalService.createDepositOrder(deposit);

        transactionService.createDepositTransaction(userByEmail, umbrella, deposit, payPalData.get("orderId"));
        return payPalData.get("approvalLink");
    }

    @Transactional
    @Override
    public StorageBox rentUmbrellaAndGetLockerInfo(String orderID,
                                                   String rentalType,
                                                   long umbrellaID,
                                                   String userEmail) throws IOException {
        boolean success = payPalService.capturePayment(orderID);

        if (success) {
            Rental rental = addRentalRecord(
                    RentalType.valueOf(rentalType),
                    userService.getUserByEmail(userEmail),
                    umbrellaService.getUmbrellaByID(umbrellaID),
                    null
            );
            Transaction transaction = transactionService.updateTransaction(
                    orderID,
                    orderID,
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
    public void returnUmbrellaAndRefundDeposit(long rentalId) throws IOException {
        Rental rental = getRentalByID(rentalId);

        if (rental == null) {
            throw new IllegalArgumentException("Invalid rental.");
        }

        Transaction depositTransaction = transactionService.findDepositByRental(rental);

        rental.setReturnedAt(LocalDateTime.now());
        double totalCost = calculateRentalCost(rental);
        double refundAmount = depositTransaction.getAmount() - totalCost;

        // TODO: logic amount < 0

        boolean refundSuccess = payPalService.refundPayment(depositTransaction.getCaptureID(), refundAmount);
        if (refundSuccess) {
            transactionService.createRefundTransaction(rental, refundAmount);
            // TODO: update rental price field
            rental.setStatus(RentalStatus.COMPLETED);
        } else {
            throw new RuntimeException("Refund failed.");
        }
    }

    @Override
    public Rental addRentalRecord(RentalType rentalType, User user, Umbrella umbrella, Discount discount) {
        Rental rental = new Rental();
        rental.setType(rentalType);
        rental.setUser(user);
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

    private double calculateRentalCost(Rental rental) {
        return switch (rental.getType()) {
            case DAILY -> rental.getUmbrella().getUmbrellaGroup().getPriceRate().getDailyRate()
                        * Duration.between(rental.getCreatedDate(), rental.getReturnedAt()).toDays();
            case HOURLY -> rental.getUmbrella().getUmbrellaGroup().getPriceRate().getHourlyRate()
                        * Duration.between(rental.getCreatedDate(), rental.getReturnedAt()).toHours();
            case null -> throw new RuntimeException("No rate specified");
        };
    }
}
