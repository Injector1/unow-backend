package com.umbrellanow.unow_backend.modules.umbrella.infrastructure;

import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.UmbrellaGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UmbrellaGroupRepository extends JpaRepository<UmbrellaGroup, Long> {
    UmbrellaGroup findByName(String name);
}
