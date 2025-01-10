package com.umbrellanow.unow_backend.modules.umbrella.domain;

import com.umbrellanow.unow_backend.modules.rate.infrastructure.entity.PriceRate;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;

import java.util.List;

public interface UmbrellaService {
    Umbrella createUmbrella(Long storageID, String groupName);
    List<Umbrella> getUmbrellasByGroupName(String umbrellaGroupName);
    Umbrella updateS3PathForUmbrella(Umbrella umbrellaToUpdate, String s3Path);
    Umbrella getUmbrellaByID(long umbrellaID);
    PriceRate getPriceRateForUmbrella(long umbrellaID);
    void markUmbrellaAsLeased(long umbrellaID);
}