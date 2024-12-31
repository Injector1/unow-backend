package com.umbrellanow.unow_backend.integrations.keycloak;

import com.umbrellanow.unow_backend.integrations.mail.EmailService;
import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
public class KeycloakServiceImpl implements KeycloakService {
    private final KeycloakConfig keycloakConfig;
    private Keycloak keycloak;
    private RealmResource realmResource;
    private final EmailService emailService;

    public KeycloakServiceImpl(KeycloakConfig keycloakConfig, EmailService emailService) {
        this.keycloakConfig = keycloakConfig;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        keycloak = Keycloak.getInstance(
                keycloakConfig.getAuthServerUrl(),
                keycloakConfig.getRealm(),
                keycloakConfig.getAdminUsername(),
                keycloakConfig.getAdminPassword(),
                keycloakConfig.getClientId(),
                keycloakConfig.getClientSecret()
        );
        realmResource = keycloak.realm(keycloakConfig.getRealm());
    }


    @Override
    public void registerUser(String email) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        Response response = realmResource.users().create(userRepresentation);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak");
        }
    }

    @Override
    public void sendOneTimeCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));

        List<UserRepresentation> users = realmResource.users().search(email);
        if (users.isEmpty()) {
            registerUser(email);
            users = realmResource.users().search(email);
        }

        String userId = users.getFirst().getId();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(code);
        credential.setTemporary(false);

        realmResource.users().get(userId).resetPassword(credential);
        emailService.sendOneTimeCode(email, code);
    }

    @Override
    public Map<String, Object> validateTokenAndExtractUserInformation(String token) {
        String introspectUrl = keycloakConfig.getAuthServerUrl() + "/realms/" +
                keycloakConfig.getRealm() + "/protocol/openid-connect/token/introspect";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakConfig.getClientId());
        body.add("client_secret", keycloakConfig.getClientSecret());
        body.add("token", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    introspectUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Boolean isActive = (Boolean) responseBody.get("active");

                if (Boolean.TRUE.equals(isActive)) {
                    return responseBody;
                } else {
                    throw new IllegalArgumentException("Token is not active");
                }
            } else {
                throw new RuntimeException("Failed to validate token. Response: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during token validation: " + e.getMessage(), e);
        }
    }

    @Override
    public String authenticateUser(String email, String code) {
        String tokenUrl = keycloakConfig.getAuthServerUrl() + "/realms/" +
                keycloakConfig.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakConfig.getClientId());
        body.add("client_secret", keycloakConfig.getClientSecret());
        body.add("username", email);
        body.add("password", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        } else {
            throw new RuntimeException("Failed to authenticate user: " + response.getStatusCode());
        }
    }
}
