package com.umbrellanow.unow_backend.repositories;

import com.umbrellanow.unow_backend.models.Umbrella;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {
}
