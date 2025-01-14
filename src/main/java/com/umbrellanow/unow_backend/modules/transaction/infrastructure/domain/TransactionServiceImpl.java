package com.umbrellanow.unow_backend.modules.transaction.infrastructure.domain;

import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.TransactionRepository;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.TransactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void createDepositTransaction(User user,
                                         Umbrella umbrella,
                                         double deposit,
                                         String orderID) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAssociatedUmbrella(umbrella);
        transaction.setAmount(deposit);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setOrderID(orderID);
        transactionRepository.save(transaction);
    }

    @Override
    public void createRefundTransaction(Rental rental, double refundAmount) {
        Transaction transaction = new Transaction();
        transaction.setUser(rental.getUser());
        transaction.setAssociatedUmbrella(rental.getUmbrella());
        transaction.setAmount(refundAmount);
        transaction.setType(TransactionType.REFUND);
        transactionRepository.save(transaction);
    }

    @Override
    public void createPaymentTransaction(Rental rental,
                                         String orderID) {
        Transaction transaction = new Transaction();
        transaction.setUser(rental.getUser());
        transaction.setAssociatedUmbrella(rental.getUmbrella());
        transaction.setAmount(rental.getTotalCost());
        transaction.setType(TransactionType.RENTAL);
        transaction.setOrderID(orderID);
        transactionRepository.save(transaction);
    }

    @Override
    public Transaction findByOrderID(String orderID) {
        return transactionRepository.findByOrderID(orderID).orElse(null);
    }

    @Override
    public Transaction findDepositByRental(Rental rental) {
        return transactionRepository.findByAssociatedRentalAndType(rental, TransactionType.DEPOSIT)
                .orElseThrow(() -> new IllegalArgumentException("Deposit transaction not found."));
    }

    @Transactional
    @Override
    public Transaction updateTransaction(String orderID, String captureID, Rental rental) {
        Transaction transaction = transactionRepository.findByOrderID(orderID).orElse(null);

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found");
        }

        transaction.setCaptureID(captureID);
        transaction.setAssociatedRental(rental);
        return transactionRepository.save(transaction);
    }
}
