package com.umbrellanow.unow_backend.modules.rental.api;

import com.umbrellanow.unow_backend.modules.rental.api.models.CaptureFinalPaymentDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.PayPalPaymentURLDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.CaptureDepositPaymentDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.RentalCostDTO;
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


    @GetMapping("/my")
    public ResponseEntity<?> getAllRentalsForCurrentUser() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();

        try {
            Collection<RentalDTO> allRentalsForUser = rentalService
                    .getAllRentalsForUser(currentUserEmail)
                    .stream()
                    .map(rental ->
                            new RentalDTO(
                                rental.getStatus() == null
                                        ? RentalStatus.ACTIVE.toString()
                                        : rental.getStatus().toString(),  // TODO: fix issue with recs from database
                                rental.getType().toString(),
                                rental.getCreatedDate(),
                                rental.getUmbrella().getId(),
                                rentalService.getRateForRental(rental)
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
    public ResponseEntity<?> getCurrentRentalCost(@RequestParam("umbrellaID") Long umbrellaID) {
        try {
            double rentalCost = rentalService.calculateRentalCost(
                    umbrellaID,
                    AuthenticationUtils.getCurrentUserEmail()
            );
            return ResponseEntity.ok(new RentalCostDTO(rentalCost));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting cost");
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

    @PostMapping("/pay-rent")
    public ResponseEntity<?> getFinalPaymentWithdrawalURL(@RequestBody UmbrellaIDDTO dto) {
        try {
            String approvalURL = rentalService.getApprovalURLForFinalPayment(
                    dto.getUmbrellaID(),
                    AuthenticationUtils.getCurrentUserEmail()
            );
            return ResponseEntity.ok(new PayPalPaymentURLDTO(approvalURL));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating final payment approval url");
        }
    }

    @PostMapping("/retrieve-locker-data")
    public ResponseEntity<?> captureDepositPaymentAndGetLockerData(@RequestBody CaptureDepositPaymentDTO captureDepositPaymentDTO) {
        try {
            StorageBox storageBox = rentalService.rentUmbrellaAndGetLockerInfo(
                    captureDepositPaymentDTO.getOrderID(),
                    captureDepositPaymentDTO.getRentalType(),
                    captureDepositPaymentDTO.getUmbrellaID(),
                    AuthenticationUtils.getCurrentUserEmail()
            );
            return ResponseEntity.ok(new StorageBoxDTO(storageBox.getNumber(), storageBox.getCode()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error capturing payment");
        }
    }

    @PostMapping("/refund-deposit")
    public ResponseEntity<?> captureFinalPaymentAndRefundDeposit(@RequestBody CaptureFinalPaymentDTO captureFinalPaymentDTO) {
        try {
            StorageBox storageBox = rentalService.returnUmbrellaAndGetLockerInfo(
                    captureFinalPaymentDTO.getOrderID(),
                    captureFinalPaymentDTO.getUmbrellaID(),
                    AuthenticationUtils.getCurrentUserEmail()
            );
            return ResponseEntity.ok(new StorageBoxDTO(storageBox.getNumber(), storageBox.getCode()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error capturing payment: " + e.getMessage());
        }
    }
}

