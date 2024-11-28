package com.umbrellanow.unow_backend.repositories;

import com.umbrellanow.unow_backend.models.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
