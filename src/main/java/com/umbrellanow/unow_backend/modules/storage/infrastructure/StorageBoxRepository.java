package com.umbrellanow.unow_backend.modules.storage.infrastructure;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface StorageBoxRepository extends JpaRepository<StorageBox, Long> {
    @Query("select b from StorageBox b where b.isEmpty = true")
    Collection<StorageBox> findAllEmpty();
}
