package com.umbrellanow.unow_backend.modules.discount.infrastructure;

import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
