//package com.backend.puntoxpress.configuration;
//
//import com.backend.puntoxpress.service.JwtTokenProviderService;
//import com.backend.puntoxpress.service.UserService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtTokenProviderService jwtTokenProviderService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
//        try {
//            String token = extractToken(request);
//            if (token != null && jwtTokenProviderService.validateToken(token)) {
//                String username = jwtTokenProviderService.getUsernameFromToken(token);
//                UserDetails userDetails = userService.loadUserByUsername(username);
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (Exception ex) {
//            System.out.println("Could not set user authentication: " + ex.getMessage());
//        }
//
//        try {
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String extractToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // Remove "Bearer " prefix
//        }
//        return null;
//    }
//}