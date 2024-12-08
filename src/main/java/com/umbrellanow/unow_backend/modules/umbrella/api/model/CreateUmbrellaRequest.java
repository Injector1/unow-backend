package com.umbrellanow.unow_backend.modules.umbrella.api.model;

public class CreateUmbrellaRequest {
    private String umbrellaGroupName;
    private long storageID;

    public String getUmbrellaGroupName() {
        return umbrellaGroupName;
    }

    public long getStorageID() {
        return storageID;
    }
}
