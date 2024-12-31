package com.umbrellanow.unow_backend.modules.storage.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorageBoxResponse {
    private long id;
    private int boxNumber;
    private int boxCode;
}
