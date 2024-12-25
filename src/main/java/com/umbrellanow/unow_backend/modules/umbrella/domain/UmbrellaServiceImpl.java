package com.umbrellanow.unow_backend.modules.umbrella.domain;

import com.umbrellanow.unow_backend.integrations.s3.S3Service;
import com.umbrellanow.unow_backend.modules.rate.infrastructure.PriceRateRepository;
import com.umbrellanow.unow_backend.modules.rate.infrastructure.entity.PriceRate;
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
    private final UmbrellaRepository umbrellaRepository;
    private final UmbrellaGroupRepository umbrellaGroupRepository;
    private final StorageBoxRepository storageBoxRepository;
    private final S3Service s3Service;
    private final PriceRateRepository priceRateRepository;

    @Autowired
    public UmbrellaServiceImpl(UmbrellaRepository umbrellaRepository,
                               StorageBoxRepository storageBoxRepository,
                               UmbrellaGroupRepository umbrellaGroupRepository,
                               PriceRateRepository priceRateRepository,
                               S3Service s3Service) {
        this.umbrellaRepository = umbrellaRepository;
        this.storageBoxRepository = storageBoxRepository;
        this.umbrellaGroupRepository = umbrellaGroupRepository;
        this.priceRateRepository = priceRateRepository;
        this.s3Service = s3Service;
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

        Umbrella savedUmbrella = umbrellaRepository.save(umbrella);
        storageBoxRepository.save(storageBox);

        String s3PathForUmbrella = s3Service.createPath("umbrella", umbrella.getId().toString());
        updateS3PathForUmbrella(savedUmbrella, s3PathForUmbrella);
    }


    @Transactional
    @Override
    public void updateS3PathForUmbrella(Umbrella umbrellaToUpdate, String s3Path) {
        umbrellaToUpdate.setS3Path(s3Path);
        umbrellaRepository.save(umbrellaToUpdate);
    }


    @Override
    public List<Umbrella> getUmbrellasByGroupName(String umbrellaGroupName) {
        UmbrellaGroup umbrellaGroup = umbrellaGroupRepository.findByName(umbrellaGroupName);
        return umbrellaRepository.findAllByUmbrellaGroup(umbrellaGroup);
    }

    @Override
    public Umbrella getUmbrellaByID(String umbrellaID) {
        return Optional.of(umbrellaRepository.findById(Long.parseLong(umbrellaID))).orElse(null).get();
    }

    @Override
    public PriceRate getPriceRateForUmbrella(String umbrellaID) {
        Umbrella umbrellaByID = getUmbrellaByID(umbrellaID);
        return umbrellaByID.getUmbrellaGroup().getPriceRate();
    }
}
