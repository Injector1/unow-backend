package com.umbrellanow.unow_backend.modules.users.api;


import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.modules.users.infrastructure.UserRepository;
import com.umbrellanow.unow_backend.security.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserRepository userRepository;

    @Autowired
    public UsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<User> getAllUser() {
        System.out.println(AuthenticationUtils.getCurrentUserId());
        System.out.println(AuthenticationUtils.getCurrentUserEmail());
        return userRepository.findAll();
    }
}
