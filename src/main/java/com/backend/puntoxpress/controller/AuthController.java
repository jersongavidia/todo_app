package com.backend.puntoxpress.controller;

import com.backend.puntoxpress.Dto.LoginDTO;
import com.backend.puntoxpress.Dto.RegisterDTO;
import com.backend.puntoxpress.service.AuthService;
import com.backend.puntoxpress.service.JwtTokenProviderService;
import com.backend.puntoxpress.service.UserService;
import com.backend.puntoxpress.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
//    @Autowired
//    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProviderService jwtTokenProviderService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginDTO loginCredentials) {
        final TokenResponse tokenResponse = authService.loginUser(loginCredentials);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody final RegisterDTO registerDTO) {
        final TokenResponse tokenResponse = authService.registerUser(registerDTO);
        return ResponseEntity.ok(tokenResponse);
        //ResponseEntity<Map<String, String>> response = userService.registerUser(userDTO);
        //return (ResponseEntity<Map<String, String>>) userService.registerUser(userDTO);
        //return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return authService.refreshToken(authHeader);
    }

}
