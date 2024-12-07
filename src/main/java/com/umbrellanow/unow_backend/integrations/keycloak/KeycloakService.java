package com.umbrellanow.unow_backend.integrations.keycloak;

public interface KeycloakService {
    void registerUser(String email);
    String authenticateUser(String email, String code);
    void sendOneTimeCode(String email);
    boolean isTokenValid(String token);
}
