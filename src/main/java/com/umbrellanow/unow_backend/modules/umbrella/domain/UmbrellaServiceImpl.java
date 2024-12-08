package com.umbrellanow.unow_backend.modules.umbrella.domain;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.StorageBoxRepository;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.UmbrellaGroupRepository;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.UmbrellaRepository;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.UmbrellaGroup;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UmbrellaServiceImpl implements UmbrellaService {
    private UmbrellaRepository umbrellaRepository;
    private UmbrellaGroupRepository umbrellaGroupRepository;
    private StorageBoxRepository storageBoxRepository;

    @Autowired
    public UmbrellaServiceImpl(UmbrellaRepository umbrellaRepository,
                               StorageBoxRepository storageBoxRepository,
                               UmbrellaGroupRepository umbrellaGroupRepository) {
        this.umbrellaRepository = umbrellaRepository;
        this.storageBoxRepository = storageBoxRepository;
        this.umbrellaGroupRepository = umbrellaGroupRepository;
    }

    @Override
    @Transactional
    public void createUmbrella(Long storageID, String groupName) {
        Umbrella umbrella = new Umbrella();

        StorageBox storageBox = Optional.of(storageBoxRepository.findById(storageID))
                .get()
                .orElseThrow(() -> new IllegalArgumentException("StorageBox not found"));

        UmbrellaGroup umbrellaGroup = Optional.ofNullable(umbrellaGroupRepository.findByName(groupName))
                .orElseThrow(() -> new IllegalArgumentException("Umbrella group not found"));

        if (!storageBox.getIsEmpty()) {
            throw new IllegalArgumentException("Storage box is already full");
        }

        umbrella.setCurrentlyLeased(false);
        umbrella.setStorageBox(storageBox);
        umbrella.setUmbrellaGroup(umbrellaGroup);

        storageBox.setStoredUmbrella(umbrella);
        storageBox.setIsEmpty(false);

        umbrellaRepository.save(umbrella);
        storageBoxRepository.save(storageBox);
    }


    @Override
    public List<Umbrella> getUmbrellasByGroupName(String umbrellaGroupName) {
        UmbrellaGroup umbrellaGroup = umbrellaGroupRepository.findByName(umbrellaGroupName);
        return umbrellaRepository.findAllByUmbrellaGroup(umbrellaGroup);
    }
}
