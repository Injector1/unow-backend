package com.umbrellanow.unow_backend.modules.rental.infrastructure;

import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByUser_id(long userID);
    List<Rental> findAllByUmbrella_id(long umbrellaID);
}
