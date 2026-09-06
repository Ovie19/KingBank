package dev.fantasma.kingbank.dtos.request;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String name;
    private String username;
    private String password;
}
