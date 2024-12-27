package com.umbrellanow.unow_backend.services;

import com.umbrellanow.unow_backend.modules.users.infrastructure.UserRepository;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.UserGroup;
import com.umbrellanow.unow_backend.shared.enumeration.UserStatus;
import com.umbrellanow.unow_backend.shared.scalars.EmailAddress;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUserGroup(UserGroup.ADMIN);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setEmail(new EmailAddress("aaa@gmail.com"));

        userRepository.save(user);

        User byEmail = userRepository.findByEmail(user.getEmail());

        assertNotNull(byEmail);
    }
}
