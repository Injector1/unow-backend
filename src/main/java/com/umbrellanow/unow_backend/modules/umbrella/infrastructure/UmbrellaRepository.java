package com.umbrellanow.unow_backend.modules.umbrella.infrastructure;

import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.UmbrellaGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {
    List<Umbrella> findAllByUmbrellaGroup(UmbrellaGroup umbrellaGroup);
}
