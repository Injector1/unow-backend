package com.umbrellanow.unow_backend.modules.rental.domain;

import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.RentalType;

import java.util.List;

public interface RentalService {
    Rental getRentalByID(long rentalID);
    Rental addRentalRecord(RentalType rentalType, User user, Umbrella umbrella, Discount discount);
    List<Rental> getAllRentalsByUserID(long userID);
    List<Rental> getAllRentalsByUmbrellaID(long umbrellaID);
}
