package com.umbrellanow.unow_backend.modules.transaction.infrastructure;

import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
