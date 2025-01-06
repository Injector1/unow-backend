package com.umbrellanow.unow_backend.modules.umbrella.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUmbrellaDTO {
    private String umbrellaGroupName;
    private long storageID;
}
