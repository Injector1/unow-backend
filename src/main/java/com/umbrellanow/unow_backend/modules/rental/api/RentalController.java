package com.umbrellanow.unow_backend.modules.rental.api;

import com.umbrellanow.unow_backend.modules.rental.api.models.PayPalPaymentURLDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.CapturePaymentDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.RentalDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.UmbrellaIDDTO;
import com.umbrellanow.unow_backend.modules.rental.domain.RentalService;
import com.umbrellanow.unow_backend.modules.storage.api.models.StorageBoxDTO;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.security.utils.AuthenticationUtils;
import com.umbrellanow.unow_backend.shared.enumeration.RentalStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/rental")
public class RentalController {
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    private double getRate(Rental rental) {
        if (rental.getType() == RentalType.DAILY) {
            return rental.getUmbrella().getUmbrellaGroup().getPriceRate().getDailyRate();
        } else {
            return rental.getUmbrella().getUmbrellaGroup().getPriceRate().getHourlyRate();
        }
    }


    @GetMapping("/my")
    public ResponseEntity<?> getAllRentalsForCurrentUser() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();

        try {
            Collection<RentalDTO> allRentalsForUser = rentalService
                    .getAllRentalsForUser(currentUserEmail)
                    .stream()
                    .map(rental -> new RentalDTO(
                                    rental.getStatus() == null ? RentalStatus.ACTIVE.toString() : rental.getStatus().toString(),
                                    rental.getType().toString(),
                                    rental.getCreatedDate(),
                                    rental.getUmbrella().getId(),
                                    getRate(rental)
                            )
                    ).toList();

            return ResponseEntity.ok(allRentalsForUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving rentals");
        }

    }

    @GetMapping("/rental-cost")
    public ResponseEntity<Double> getCurrentRentalCost(@RequestParam Long rentalId) {
        try {
            double rentalCost = rentalService.calculateRentalCost(rentalId);
            return ResponseEntity.ok(rentalCost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0.0);
        }
    }


    @PostMapping("/deposit")
    public ResponseEntity<?> getDepositWithdrawalURL(@RequestBody UmbrellaIDDTO dto) {
        try {
            String approvalURL = rentalService.getApprovalURLForUmbrellaDeposit(dto.getUmbrellaID());
            return ResponseEntity.ok(new PayPalPaymentURLDTO(approvalURL));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating deposit approval url");
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<?> capturePayment(@RequestBody CapturePaymentDTO capturePaymentDTO) {
        try {
            StorageBox storageBox = rentalService.rentUmbrellaAndGetLockerInfo(
                    capturePaymentDTO.getOrderID(),
                    capturePaymentDTO.getRentalType(),
                    capturePaymentDTO.getUmbrellaID(),
                    AuthenticationUtils.getCurrentUserEmail()
            );
            return ResponseEntity.ok(new StorageBoxDTO(storageBox.getNumber(), storageBox.getCode()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error capturing payment");
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@RequestBody Long rentalId) {
        try {
            rentalService.returnUmbrellaAndRefundDeposit(rentalId);
            return ResponseEntity.ok("Deposit refunded");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing refund: " + e.getMessage());
        }
    }
}

