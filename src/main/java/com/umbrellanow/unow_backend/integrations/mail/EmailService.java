package com.umbrellanow.unow_backend.integrations.mail;

public interface EmailService {
    void sendOneTimeCode(String email, String code);
}
