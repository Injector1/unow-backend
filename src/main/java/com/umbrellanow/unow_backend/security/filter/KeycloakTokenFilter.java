package com.umbrellanow.unow_backend.security.filter;

import com.umbrellanow.unow_backend.integrations.keycloak.KeycloakService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class KeycloakTokenFilter extends OncePerRequestFilter {
    private final KeycloakService keycloakService;


    public KeycloakTokenFilter(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (servletPath.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header.");
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            // Validate the token with Keycloak
            if (!keycloakService.isTokenValid(token)) {
                throw new IllegalArgumentException();
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "user",
                    null,
                    List.of(() -> "ROLE_USER")
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token.");
        }
    }
}
