package com.umbrellanow.unow_backend.integrations.keycloak;

import java.util.Map;

public interface KeycloakService {
    void registerUser(String email);
    String authenticateUser(String email, String code);
    void sendOneTimeCode(String email);
    Map<String, Object> validateTokenAndExtractUserInformation(String token);
}
