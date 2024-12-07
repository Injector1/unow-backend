package com.umbrellanow.unow_backend.modules.storage.infrastructure;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageBoxRepository extends JpaRepository<StorageBox, Long> {
}
