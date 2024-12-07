package com.umbrellanow.unow_backend.modules.rate.infrastructure;

import com.umbrellanow.unow_backend.modules.rate.infrastructure.entity.PriceRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRateRepository extends JpaRepository<PriceRate, Long> {
}
