package com.backend.puntoxpress.Dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String token;
}
