package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.LoginDTO;
import com.backend.puntoxpress.Dto.RegisterDTO;
import com.backend.puntoxpress.controller.TokenResponse;
import com.backend.puntoxpress.entity.Token;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.repository.TokenRepository;
import com.backend.puntoxpress.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

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

    public TokenResponse loginUser(LoginDTO loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new TokenResponse(jwtToken,refreshToken);
    }

    public TokenResponse refreshToken(final String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("Invalid Bearer token");
        }
        final String refreshToken = authHeader.substring(7);
        final String userEmail = getUsernameFromToken(refreshToken);
        if(userEmail == null){
            logger.error("Invalid Refresh Token: {}",refreshToken);
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        final User user = userRepository.findByUsername(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));
        if(!isTokenValid(refreshToken, user)){
            logger.error("Invalid Refresh Token: {}", refreshToken);
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        final String accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }
    public boolean isTokenValid(final String token, final User user){
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(final String token){
        return getExpirationFromToken(token).before(new Date());
    }
    public Date getExpirationFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
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

    private void revokeAllUserTokens(final User user){
         List<Token> validUserTokens = tokenRepository.findAllNotExpiredNorRevokedByUserId(user.getId());
         if(!validUserTokens.isEmpty()){
             for(Token token : validUserTokens){
                 token.setExpired(true);
                 token.setRevoked(true);
             }
             tokenRepository.saveAll(validUserTokens);
         }
    }

}
