package com.umbrellanow.unow_backend.integrations.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {
    private String authServerUrl;     // Keycloak server URL
    private String realm;             // Realm name
    private String clientId;          // Client ID for OAuth2
    private String clientSecret;      // Client Secret for OAuth2
    private String adminUsername;     // Admin username for REST API
    private String adminPassword;     // Admin password for REST API
}
