package com.umbrellanow.unow_backend.services;

import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import com.umbrellanow.unow_backend.modules.rental.domain.RentalService;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.RentalRepository;
import com.umbrellanow.unow_backend.modules.rental.infrastructure.entity.Rental;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.UserRepository;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.RentalType;
import com.umbrellanow.unow_backend.shared.enumeration.UserGroup;
import com.umbrellanow.unow_backend.shared.enumeration.UserStatus;
import com.umbrellanow.unow_backend.shared.scalars.EmailAddress;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Transactional
public class RentalServiceTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testAddRentalRecord() {
        User user = new User();
        Umbrella umbrella = new Umbrella();
        Discount discount = new Discount();
        RentalType rentalType = RentalType.DAILY;

        Rental result = rentalService.addRentalRecord(rentalType, user, umbrella, discount);

        assertNotNull(result);
    }

    @Test
    public void testFindByUserID() {
        User user = new User();
        user.setUserGroup(UserGroup.ADMIN);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setEmail(new EmailAddress("aaa@gmail.com"));
        User savedUser = userRepository.save(user);

        Umbrella umbrella = new Umbrella();
        umbrella.setId(1L);

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setUmbrella(umbrella);
        rental.setType(RentalType.DAILY);

        Rental saved = rentalRepository.save(rental);

        assertEquals(saved.getId(), rentalService.getAllRentalsByUserID(savedUser.getId()).getFirst().getId());
    }

    @Test
    public void testFindByUmbrellaID() {
        User user = new User();
        user.setUserGroup(UserGroup.ADMIN);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setEmail(new EmailAddress("aaa@gmail.com"));
        User savedUser = userRepository.save(user);

        Umbrella umbrella = new Umbrella();
        umbrella.setId(1L);

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setUmbrella(umbrella);
        rental.setType(RentalType.DAILY);

        Rental saved = rentalRepository.save(rental);

        Rental first = rentalService.getAllRentalsByUmbrellaID(1L).getFirst();
        assertNotNull(first);
        assertEquals(saved.getId(), first.getId());
        assertEquals(savedUser.getId(), first.getUser().getId());
    }
}