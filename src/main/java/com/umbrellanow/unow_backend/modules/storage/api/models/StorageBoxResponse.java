package com.umbrellanow.unow_backend.modules.storage.api.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageBoxResponse {
    private long id;
    private int boxNumber;
    private int boxCode;
}
