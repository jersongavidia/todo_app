package com.backend.puntoxpress.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtTokenProviderService {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY; //Secret via Environment Variables
    @Value("${application.security.jwt.expiration}")
    private long EXPIRATION_TIME; // 1 day
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION_MILLIS; // 7 days

    Logger logger = LoggerFactory.getLogger(JwtTokenProviderService.class);

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token expired
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // Token malformed
            return false;
        } catch (Exception e) {
            // Other issues
            return false;
        }
    }

}