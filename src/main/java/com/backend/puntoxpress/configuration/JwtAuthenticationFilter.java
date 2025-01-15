package com.backend.puntoxpress.configuration;

import com.backend.puntoxpress.entity.Token;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.repository.TokenRepository;
import com.backend.puntoxpress.repository.UserRepository;
import com.backend.puntoxpress.service.AuthService;
import com.backend.puntoxpress.service.CustomUserDetailsService;
import com.backend.puntoxpress.service.JwtTokenProviderService;
import com.backend.puntoxpress.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProviderService jwtTokenProviderService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getServletPath().contains("/auth")){
            filterChain.doFilter(request,response);
            return;
        }
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        final String jwtToken = authHeader.substring(7);
        final String userEmail = jwtTokenProviderService.getUsernameFromToken(jwtToken);
        if(userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null){
            return;
        }
        final Token token = tokenRepository.findByToken(jwtToken);
        if(token==null || token.isExpired() || token.isRevoked()){
            filterChain.doFilter(request, response);
            return;
        }
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        final Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if(user.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
        final boolean isTokenValid = authService.isTokenValid(jwtToken, user.get());
        if(!isTokenValid){
            return;
        }
        final var authToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}