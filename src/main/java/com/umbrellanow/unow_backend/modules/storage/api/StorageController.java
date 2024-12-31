package com.umbrellanow.unow_backend.modules.storage.api;

import com.umbrellanow.unow_backend.modules.storage.api.models.StorageBoxResponse;
import com.umbrellanow.unow_backend.modules.storage.domain.StorageService;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("/empty-box")
    public ResponseEntity<?> getEmptyBox() {
        StorageBox emptyStorageBox = storageService.findEmptyStorageBox();
        if (emptyStorageBox == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty box not found");
        }
        return ResponseEntity.ok(
                new StorageBoxResponse(
                        emptyStorageBox.getId(),
                        emptyStorageBox.getNumber(),
                        emptyStorageBox.getCode()
                )
        );
    }
}
