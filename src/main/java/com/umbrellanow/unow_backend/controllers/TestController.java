package com.umbrellanow.unow_backend.controllers;

import com.umbrellanow.unow_backend.models.User;
import com.umbrellanow.unow_backend.models.scalars.EmailAddress;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public User test() {
        User user = new User();
        user.setId(1l);
        user.setEmail(new EmailAddress("abc@gmail.com"));
        return user;
    }
}
