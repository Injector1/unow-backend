package com.umbrellanow.unow_backend.modules.rental.domain;

import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.RentalType;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface RentalService {
    Rental getRentalByID(long rentalID);

    @Transactional
    void returnUmbrellaAndRefundDeposit(long rentalId) throws IOException;

    Rental addRentalRecord(RentalType rentalType, User user, Umbrella umbrella, Discount discount);
    List<Rental> getAllRentalsByUserID(long userID);
    List<Rental> getAllRentalsByUmbrellaID(long umbrellaID);
    String getApprovalURLForUmbrellaDeposit(long umbrellaID) throws IOException;
    StorageBox rentUmbrellaAndGetLockerInfo(String orderID,
                                            String rentalType,
                                            long umbrellaID,
                                            String userEmail) throws IOException;
    Collection<Rental> getAllRentalsForUser(String userEmail);
}
