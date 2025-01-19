package com.umbrellanow.unow_backend.modules.storage.domain;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;

public interface StorageService {
    StorageBox findEmptyStorageBox();
    StorageBox markStorageAsEmpty(StorageBox box);
    StorageBox setAssociatedUmbrellaForBox(StorageBox box, Umbrella umbrella);
}
