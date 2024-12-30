package com.umbrellanow.unow_backend.modules.storage.domain;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;

public interface StorageService {
    StorageBox findEmptyStorageBox();
}
