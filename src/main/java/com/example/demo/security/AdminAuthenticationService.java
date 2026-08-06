package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthenticationService implements UserDetailsService {

    private static final String BCRYPT_PREFIX = "{bcrypt}";

    private final String adminUsername;
    private final String adminPasswordHash;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthenticationService(
            @Value("${app.security.admin-username}") String adminUsername,
            @Value("${app.security.admin-password-hash}") String adminPasswordHash,
            PasswordEncoder passwordEncoder) {
        if (adminUsername == null || adminUsername.isBlank()) {
            throw new IllegalStateException("ADMIN_USERNAME es obligatorio");
        }
        if (adminPasswordHash == null || adminPasswordHash.isBlank()) {
            throw new IllegalStateException("ADMIN_PASSWORD_HASH es obligatorio");
        }
        this.adminUsername = adminUsername;
        this.adminPasswordHash = normalizeHash(adminPasswordHash);
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String rawPassword) {
        return isAdmin(username)
                && rawPassword != null
                && passwordEncoder.matches(rawPassword, adminPasswordHash);
    }

    public boolean isAdmin(String username) {
        return adminUsername.equals(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!isAdmin(username)) {
            throw new UsernameNotFoundException("Administrador no encontrado");
        }
        return User.withUsername(adminUsername)
                .password(adminPasswordHash)
                .roles("ADMIN")
                .build();
    }

    private String normalizeHash(String passwordHash) {
        return passwordHash.startsWith(BCRYPT_PREFIX)
                ? passwordHash.substring(BCRYPT_PREFIX.length())
                : passwordHash;
    }
}
