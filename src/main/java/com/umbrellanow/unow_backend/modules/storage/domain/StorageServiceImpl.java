package com.umbrellanow.unow_backend.modules.storage.domain;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.StorageBoxRepository;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
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

    @Override
    public StorageBox setAssociatedUmbrellaForBox(StorageBox box, Umbrella umbrella) {
        box.setStoredUmbrella(umbrella);
        box.setIsEmpty(false);
        return storageBoxRepository.save(box);
    }

    @Override
    public StorageBox markStorageAsEmpty(StorageBox box) {
        box.setIsEmpty(true);
        return storageBoxRepository.save(box);
    }
}
