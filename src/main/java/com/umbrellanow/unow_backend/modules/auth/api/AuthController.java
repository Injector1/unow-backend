package com.umbrellanow.unow_backend.modules.auth.api;

import com.umbrellanow.unow_backend.modules.auth.api.model.AuthenticationRequest;
import com.umbrellanow.unow_backend.modules.auth.api.model.AuthenticationResponse;
import com.umbrellanow.unow_backend.modules.auth.domain.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to send a one-time code to the user's email.
     * @param request Contains the user's email.
     * @return Response indicating the code was sent.
     */
    @PostMapping("/send-code")
    public ResponseEntity<String> sendOneTimeCode(@RequestBody AuthenticationRequest request) {
        try {
            authService.registerUser(request.getEmail());
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
            String accessToken = authService.authenticateUser(request.getEmail(), request.getCode());
            AuthenticationResponse response = new AuthenticationResponse(accessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or code. Please try again.");
        }
    }
}
