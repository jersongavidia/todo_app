package com.backend.puntoxpress.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
public class LoginDTO {
    private String username;
    private String password;
    private String email;
    private String token;
    private String refreshToken;

}