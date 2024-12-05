package com.umbrellanow.unow_backend.services;

public interface AuthService {
    void registerUser(String email);
    String authenticateUser(String email, String code);
}
