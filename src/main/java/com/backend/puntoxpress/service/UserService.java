package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.UserDTO;
import com.backend.puntoxpress.Dto.LoginDTO;
import com.backend.puntoxpress.entity.Token;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.exception.UserNotFoundException;
import com.backend.puntoxpress.repository.TokenRepository;
import com.backend.puntoxpress.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProviderService jwtTokenProviderService;

    private String jwtToken;
    private String refreshToken;
    private Token generatedToken;

    public UserDTO registerUser(UserDTO userDTO) {
        // Check if a user with the same username or email already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent() || userRepository.findByEmail(userDTO.getEmail()).isPresent() ) {
            throw new RuntimeException("Username or email already taken.");
        }

        // Create a new User entity and set its fields from the DTO
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encode the password

        logger.info("BCrypt password: " + user.getPassword());
        // Save the user entity to the database
        User savedUser = userRepository.save(user);
        try{
            jwtToken = jwtTokenProviderService.generateToken(savedUser.getUsername());
            refreshToken = jwtTokenProviderService.generateRefresToken(savedUser.getUsername(), userDTO.getPassword());
            generatedToken = saveUserToken(savedUser, jwtToken);
        }catch (Exception ex){
            logger.error(ex.getMessage());
            userRepository.delete(savedUser);
        }
        // Map the saved user entity to a UserDTO and return it
        return mapUserToDTO(savedUser);
    }
    private Token saveUserToken(User user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        return tokenRepository.save(token);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return mapUserToDTO(user);
    }

    public boolean getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        mapUserToDTO(user);
        return true;
    }

    public UserDTO loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loginDTO.getUsername()));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid username or password");
        }

        return mapUserToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapUserToDTO).collect(Collectors.toList());
    }

    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(jwtToken);
        return userDTO;
    }
//    private UserDTO mapTokenToDTO(Token token) {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setId(user.getId());
//        userDTO.setUsername(user.getUsername());
//        userDTO.setEmail(user.getEmail());
//        return userDTO;
//    }
}
