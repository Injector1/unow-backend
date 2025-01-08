package com.umbrellanow.unow_backend.modules.users.domain;

import com.umbrellanow.unow_backend.modules.users.infrastructure.UserRepository;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.scalars.EmailAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByID(long userID) {
        return userRepository.findById(userID).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(new EmailAddress(email));
    }
}
