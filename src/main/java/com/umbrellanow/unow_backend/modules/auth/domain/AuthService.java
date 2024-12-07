package com.umbrellanow.unow_backend.modules.auth.domain;

public interface AuthService {
    void registerUser(String email);
    String authenticateUser(String email, String code);
}
