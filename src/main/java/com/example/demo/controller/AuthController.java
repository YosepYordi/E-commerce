package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.security.AdminAuthenticationService;
import com.example.demo.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminAuthenticationService adminAuthenticationService;
    private final JwtService jwtService;

    public AuthController(AdminAuthenticationService adminAuthenticationService, JwtService jwtService) {
        this.adminAuthenticationService = adminAuthenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!adminAuthenticationService.authenticate(request.username(), request.password())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        String token = jwtService.generateToken(request.username());
        return ResponseEntity.ok(new TokenResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds()
        ));
    }
}
