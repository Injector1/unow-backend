package com.umbrellanow.unow_backend.modules.rental.domain;

import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.RentalRepository;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.RentalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;


    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }


    @Override
    public Rental getRentalByID(long rentalID) {
        return rentalRepository.findById(rentalID).orElse(null);
    }

    @Override
    public Rental addRentalRecord(RentalType rentalType, User user, Umbrella umbrella, Discount discount) {
        Rental rental = new Rental();
        rental.setType(rentalType);
        rental.setUser(user);
        rental.setUmbrella(umbrella);
        rental.setDiscount(discount);
        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> getAllRentalsByUserID(long userID) {
        return rentalRepository.findAllByUser_id(userID);
    }

    @Override
    public List<Rental> getAllRentalsByUmbrellaID(long umbrellaID) {
        return rentalRepository.findAllByUmbrella_id(umbrellaID);
    }
}
