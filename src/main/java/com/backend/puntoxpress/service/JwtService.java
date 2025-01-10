package com.backend.puntoxpress.service;

import com.backend.puntoxpress.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY; //Secret via Environment Variables
    @Value("${application.security.jwt.expiration}")
    private long EXPIRATION_TIME; // 1 day
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION_MILLIS; // 7 days

    public String generateToken(final User savedUser) {
        return Jwts.builder()
                .setSubject(savedUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }
    public String generateRefreshToken(final User savedUser) {
        return Jwts.builder()
                .setSubject(savedUser.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRATION_TIME)) // 1 hour
                .signWith(getSignInKey())
                .compact();
    }
    private String buildToken(final User user, final long expiration){
        return Jwts.builder()
                .setId(user.getId().toString())
                .setClaims(Map.of("name", user.getUsername()))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }
    public SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
