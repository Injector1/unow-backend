package com.umbrellanow.unow_backend.modules.umbrella.api;

import com.umbrellanow.unow_backend.integrations.s3.S3Service;
import com.umbrellanow.unow_backend.modules.rate.infrastructure.entity.PriceRate;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.CreateUmbrellaDTO;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.GetUmbrellaByGroupNameDTO;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.GetUmbrellaByIDDTO;
import com.umbrellanow.unow_backend.modules.umbrella.api.model.UmbrellaPriceRateDTO;
import com.umbrellanow.unow_backend.modules.umbrella.domain.UmbrellaService;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/umbrella")
public class UmbrellaController {
    private final UmbrellaService umbrellaService;
    private final S3Service storageService;

    @Autowired
    public UmbrellaController(UmbrellaService umbrellaService,
                              S3Service storageService) {
        this.umbrellaService = umbrellaService;
        this.storageService = storageService;
    }


    @GetMapping("/umbrellas")
    public ResponseEntity<?> getAllUmbrellasByUmbrellaGroup(@RequestBody GetUmbrellaByGroupNameDTO request) {
        List<Umbrella> umbrellasByGroupName = umbrellaService.getUmbrellasByGroupName(request.getGroupName());
        return ResponseEntity.ok(umbrellasByGroupName);
    }

    @PostMapping("/create-umbrella")
    public ResponseEntity<?> createUmbrellaByGroupName(@RequestBody CreateUmbrellaDTO request) {
        try {
            umbrellaService.createUmbrella(request.getStorageID(), request.getUmbrellaGroupName());
            return ResponseEntity.ok("");
        } catch (IllegalArgumentException ex) {
            // TODO: Global exception handler
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/umbrella")
    public ResponseEntity<?> getUmbrellaDailyRate(@RequestBody GetUmbrellaByIDDTO request) {
        return ResponseEntity.ok(umbrellaService.getUmbrellaByID(request.getId()));
    }

    @GetMapping("/umbrella-price-rate")
    public ResponseEntity<UmbrellaPriceRateDTO> getUmbrellaPriceRate(@RequestParam("id") Long umbrellaID) {
        // TODO: add validator for parameter
        PriceRate priceRateForUmbrella = umbrellaService.getPriceRateForUmbrella(umbrellaID);
        UmbrellaPriceRateDTO umbrellaPriceRateResponse = new UmbrellaPriceRateDTO();
        umbrellaPriceRateResponse.setDailyRate(priceRateForUmbrella.getDailyRate());
        umbrellaPriceRateResponse.setHourlyRate(priceRateForUmbrella.getHourlyRate());
        umbrellaPriceRateResponse.setDeposit(priceRateForUmbrella.getDeposit());
        return ResponseEntity.ok(umbrellaPriceRateResponse);
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadUmbrellaPhoto(@RequestPart("id") String id,
                                                 @RequestPart("photo") MultipartFile photo) {
        Umbrella umbrellaByID = umbrellaService.getUmbrellaByID(Long.parseLong(id));

        if (umbrellaByID == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Umbrella with ID " + id + " not found.");
        }

        if (!isValidImage(photo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Uploaded file is not a valid image.");
        }

        try {
            storageService.uploadFile(
                    umbrellaByID.getS3Path(),
                    photo.getOriginalFilename(),
                    photo.getInputStream(),
                    photo.getSize(),
                    photo.getContentType()
            );

            return ResponseEntity.ok("Photo uploaded successfully.");
        } catch (IOException exception) {
            exception.printStackTrace();  // TODO: fix error logging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while uploading the photo.");
        }
    }


    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }
}
