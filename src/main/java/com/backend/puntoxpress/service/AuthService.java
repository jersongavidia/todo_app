package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.LoginDTO;
import com.backend.puntoxpress.Dto.RegisterDTO;
import com.backend.puntoxpress.controller.TokenResponse;
import com.backend.puntoxpress.entity.Token;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.repository.TokenRepository;
import com.backend.puntoxpress.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TokenRepository tokenRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtService jwtService;

    public TokenResponse registerUser(RegisterDTO registerDTO) {
        User user = User.builder()
                .username(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();
        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse loginUser(LoginDTO loginCredentials) {
        return null;
    }

    private void saveUserToken(User user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    public TokenResponse refreshToken(String authHeader) {
        return null;
    }
}
