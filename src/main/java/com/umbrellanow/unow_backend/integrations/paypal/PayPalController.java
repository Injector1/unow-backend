package com.umbrellanow.unow_backend.integrations.paypal;

import com.umbrellanow.unow_backend.modules.rental.domain.RentalService;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.domain.TransactionService;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.domain.UserService;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.security.utils.AuthenticationUtils;
import com.umbrellanow.unow_backend.shared.enumeration.RentalStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/paypal")
public class PayPalController {
    private final PayPalService payPalService;
    private final UmbrellaService umbrellaService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final RentalService rentalService;

    @Autowired
    public PayPalController(PayPalService payPalService,
                            UmbrellaService umbrellaService,
                            TransactionService transactionService,
                            UserService userService,
                            RentalService rentalService) {
        this.payPalService = payPalService;
        this.umbrellaService = umbrellaService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> createDeposit(@RequestParam long umbrellaId) {
        try {
            Umbrella umbrella = umbrellaService.getUmbrellaByID(umbrellaId);
            User userByEmail = userService.getUserByEmail(AuthenticationUtils.getCurrentUserEmail());

            if (umbrella == null || umbrella.isCurrentlyLeased()) {
                return ResponseEntity.badRequest().body("Umbrella not available.");
            }

            if (userByEmail == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Double deposit = umbrella.getUmbrellaGroup().getPriceRate().getDeposit();
            String approvalUrl = payPalService.createDepositOrder(deposit);

            transactionService.createDepositTransaction(userByEmail, umbrella, deposit, approvalUrl);

            return ResponseEntity.ok(approvalUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error creating deposit order", e);
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<String> capturePayment(@RequestParam String orderId) {
        try {
            boolean success = payPalService.capturePayment(orderId);

            if (success) {
                transactionService.updateCaptureID(orderId, orderId);
                return ResponseEntity.ok("Payment captured successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment capture failed.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error capturing payment", e);
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@RequestParam Long rentalId) {
        try {
            Rental rental = rentalService.getRentalByID(rentalId);
            if (rental == null || rental.getStatus() != RentalStatus.COMPLETED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or incomplete rental.");
            }

            Transaction depositTransaction = transactionService.findDepositByRental(rental);

            double totalCost = calculateRentalCost(rental);
            double refundAmount = depositTransaction.getAmount() - totalCost;

            boolean refundSuccess = payPalService.refundPayment(depositTransaction.getCaptureID(), refundAmount);
            if (refundSuccess) {
                transactionService.createRefundTransaction(rental, refundAmount);

                return ResponseEntity.ok("Refund processed successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refund failed.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing refund: " + e.getMessage());
        }
    }

    /**
     * Helper method to calculate the rental cost.
     */
    private double calculateRentalCost(Rental rental) {
        long durationHours = Duration.between(rental.getCreatedDate(), rental.getReturnedAt()).toHours();
        double rate = rental.getUmbrella().getUmbrellaGroup().getPriceRate().getHourlyRate();
        return durationHours * rate;
    }
}

