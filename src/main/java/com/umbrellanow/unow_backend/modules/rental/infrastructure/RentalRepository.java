package com.umbrellanow.unow_backend.modules.rental.infrastructure;

import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
