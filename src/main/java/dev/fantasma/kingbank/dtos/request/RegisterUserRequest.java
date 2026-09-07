package dev.fantasma.kingbank.dtos.request;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
}
