package com.backend.puntoxpress.configuration;

import com.backend.puntoxpress.entity.Token;
import com.backend.puntoxpress.repository.TokenRepository;
import com.backend.puntoxpress.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Lazy
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Lazy
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private final TokenRepository tokenRepository;
    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Updated to use builder-based approach
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/auth/**").permitAll() // Public endpoints
                    .anyRequest().authenticated() // Secure all other endpoints
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider)
                .logout(logout ->
                    logout.logoutUrl("/api/auth/logout")
                            .addLogoutHandler((request, response, authentication) -> {
                                final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                logout(authHeader);
                            })
                            .logoutSuccessHandler((request, response, authentication) ->
                                    SecurityContextHolder.clearContext())
                );
        return http.build();
    }
    private void logout(final String token){
        if(token==null || !token.startsWith("Bearer ")){
            logger.error("Invalid token: {}", token);
            throw  new IllegalArgumentException("Invalid token");
        }
        final String jwtToken = token.substring(7);
        final Token foundToken = tokenRepository.findByToken(jwtToken);
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService(); // Implement this class to load user details
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
