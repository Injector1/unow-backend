package com.umbrellanow.unow_backend.modules.umbrella.api.model;

import lombok.Getter;

@Getter
public class CreateUmbrellaRequest {
    private String umbrellaGroupName;
    private long storageID;
}
