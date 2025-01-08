package com.umbrellanow.unow_backend.modules.transaction.infrastructure;

import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import com.umbrellanow.unow_backend.shared.enumeration.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderID(String orderID);

    Optional<Transaction> findByAssociatedRentalAndType(Rental rental, TransactionType type);

    List<Transaction> findAllByAssociatedRental(Rental rental);

    List<Transaction> findAllByUser_Id(Long userId);
}
