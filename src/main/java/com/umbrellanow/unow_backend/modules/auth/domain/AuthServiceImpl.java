package com.umbrellanow.unow_backend.modules.auth.domain;

import com.umbrellanow.unow_backend.integrations.keycloak.KeycloakService;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.enumeration.UserGroup;
import com.umbrellanow.unow_backend.shared.enumeration.UserStatus;
import com.umbrellanow.unow_backend.shared.scalars.EmailAddress;
import com.umbrellanow.unow_backend.modules.users.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    @Autowired
    public AuthServiceImpl(KeycloakService keycloakService, UserRepository userRepository) {
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void registerUser(String email) {
        User user = userRepository.findByEmail(new EmailAddress(email));

        if (user != null) {
            keycloakService.sendOneTimeCode(user.getEmail().getEmail());
            return;
        }

        User userToCreate = new User();
        userToCreate.setEmail(new EmailAddress(email));
        userToCreate.setUserStatus(UserStatus.INACTIVE);
        userToCreate.setUserGroup(UserGroup.STANDART);

        userRepository.save(userToCreate);
        keycloakService.sendOneTimeCode(email);
    }

    @Transactional
    @Override
    public String authenticateUser(String email, String code) {
        User existingUser = userRepository.findByEmail(new EmailAddress(email));

        if (existingUser == null) {
            throw new IllegalArgumentException("User not exists");
        }

        existingUser.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(existingUser);

        return keycloakService.authenticateUser(email, code);
    }
}
