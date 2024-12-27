package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.UserDTO;
import com.backend.puntoxpress.Dto.LoginDTO;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.exception.UserNotFoundException;
import com.backend.puntoxpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return mapToDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return mapToDTO(user);
    }



    public UserDTO authenticateUser(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loginDTO.getUsername()));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid username or password");
        }

        return mapToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

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

        // Save the user entity to the database
        User savedUser = userRepository.save(user);

        // Map the saved user entity to a UserDTO and return it
        return mapToDTO(savedUser);
    }


    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
