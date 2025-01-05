package com.backend.puntoxpress.Dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private String token;
    private String refreshToken;
}
