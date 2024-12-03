package com.umbrellanow.unow_backend.integrations.mail;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendOneTimeCode(String email, String code) {
        System.out.println(code);
    }
}
