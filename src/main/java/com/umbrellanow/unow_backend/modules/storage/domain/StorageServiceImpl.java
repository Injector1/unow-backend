package com.umbrellanow.unow_backend.modules.storage.domain;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.StorageBoxRepository;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {
    private final StorageBoxRepository storageBoxRepository;

    @Autowired
    public StorageServiceImpl(StorageBoxRepository storageBoxRepository) {
        this.storageBoxRepository = storageBoxRepository;
    }


    @Override
    public StorageBox findEmptyStorageBox() {
        return storageBoxRepository.findAllEmpty().stream().findFirst().orElse(null);
    }
}
