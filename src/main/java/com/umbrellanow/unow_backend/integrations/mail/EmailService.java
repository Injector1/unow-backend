package com.umbrellanow.unow_backend.integrations.mail;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
    void sendOneTimeCode(String email, String code);
}
