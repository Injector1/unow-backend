package com.umbrellanow.unow_backend.modules.umbrella.domain;

import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;

import java.util.List;

public interface UmbrellaService {
    void createUmbrella(Long storageID, String groupName);
    List<Umbrella> getUmbrellasByGroupName(String umbrellaGroupName);
    void updateS3PathForUmbrella(Umbrella umbrellaToUpdate, String s3Path);
}