package com.umbrellanow.unow_backend.controllers;

import com.umbrellanow.unow_backend.controllers.model.AuthenticationRequest;
import com.umbrellanow.unow_backend.controllers.model.AuthenticationResponse;
import com.umbrellanow.unow_backend.integrations.keycloak.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    @Autowired
    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    /**
     * Endpoint to send a one-time code to the user's email.
     * @param request Contains the user's email.
     * @return Response indicating the code was sent.
     */
    @PostMapping("/send-code")
    public ResponseEntity<String> sendOneTimeCode(@RequestBody AuthenticationRequest request) {
        try {
            keycloakService.sendOneTimeCode(request.getEmail());
            return ResponseEntity.ok("Verification code sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending verification code. Please try again.");
        }
    }

    /**
     * Endpoint to verify the one-time code and authenticate the user.
     * @param request Contains the user's email and the code.
     * @return Response with the access token if authentication is successful.
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody AuthenticationRequest request) {
        try {
            String accessToken = keycloakService.authenticateUser(request.getEmail(), request.getCode());
            AuthenticationResponse response = new AuthenticationResponse(accessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or code. Please try again.");
        }
    }
}
