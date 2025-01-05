package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.RegisterDTO;
import com.backend.puntoxpress.repository.TokenRepository;
import com.backend.puntoxpress.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TokenRepository tokenRepository;
    //private final UserRepository userRepository;

}
