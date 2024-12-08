package com.umbrellanow.unow_backend.security.filter;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class UserAuthentication extends UsernamePasswordAuthenticationToken {
    private final String keycloakUserID;
    private final String email;

    public UserAuthentication(String keycloakUserID,
                              String email,
                              Object principal,
                              Object credentials,
                              Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.keycloakUserID = keycloakUserID;
        this.email = email;
    }
}
