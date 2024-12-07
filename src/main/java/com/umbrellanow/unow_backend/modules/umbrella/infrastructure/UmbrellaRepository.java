package com.umbrellanow.unow_backend.modules.umbrella.infrastructure;

import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {
}
