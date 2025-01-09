package com.umbrellanow.unow_backend.modules.rental.api;

import com.umbrellanow.unow_backend.modules.rental.api.models.PayPalPaymentURLDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.RentalDTO;
import com.umbrellanow.unow_backend.modules.rental.api.models.UmbrellaIDDTO;
import com.umbrellanow.unow_backend.modules.rental.domain.RentalService;
import com.umbrellanow.unow_backend.modules.storage.api.models.StorageBoxDTO;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.security.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rental")
public class RentalController {
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> getDepositWithdrawalURL(@RequestBody UmbrellaIDDTO dto) {
        try {
            String approvalURL = rentalService.getApprovalURLForUmbrellaDeposit(dto.getUmbrellaID());
            return ResponseEntity.ok(new PayPalPaymentURLDTO(approvalURL));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating deposit approval url");
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<?> capturePayment(@RequestBody RentalDTO rentalDTO) {
        try {
            StorageBox storageBox = rentalService.rentUmbrellaAndGetLockerInfo(
                    rentalDTO.getOrderID(),
                    rentalDTO.getRentalType(),
                    rentalDTO.getUmbrellaID(),
                    AuthenticationUtils.getCurrentUserEmail()
            );
            return ResponseEntity.ok(new StorageBoxDTO(storageBox.getNumber(), storageBox.getCode()));
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

