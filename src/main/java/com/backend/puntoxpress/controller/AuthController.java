package com.backend.puntoxpress.controller;

import com.backend.puntoxpress.Dto.LoginDTO;
import com.backend.puntoxpress.Dto.RegisterDTO;
import com.backend.puntoxpress.service.AuthService;
import com.backend.puntoxpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginDTO loginCredentials) {
        final TokenResponse tokenResponse = authService.loginUser(loginCredentials);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody final RegisterDTO registerDTO) {
        final TokenResponse tokenResponse = authService.registerUser(registerDTO);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return authService.refreshToken(authHeader);
    }
}
