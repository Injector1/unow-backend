package com.umbrellanow.unow_backend.repositories;

import com.umbrellanow.unow_backend.models.PriceRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRateRepository extends JpaRepository<PriceRate, Long> {
}
