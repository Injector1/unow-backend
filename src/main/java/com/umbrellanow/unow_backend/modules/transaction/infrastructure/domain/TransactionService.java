package com.umbrellanow.unow_backend.modules.transaction.infrastructure.domain;

import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;

public interface TransactionService {
    void createDepositTransaction(User user, Umbrella umbrella, double deposit, String orderID);
    void createRefundTransaction(Rental rental, double refundAmount);
    Transaction findByOrderID(String orderID);
    void updateCaptureID(String orderID, String captureID);
    Transaction findDepositByRental(Rental rental);
}
