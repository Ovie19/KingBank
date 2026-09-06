package dev.fantasma.kingbank.service;

import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import dev.fantasma.kingbank.exception.KingBankException;

public interface UserService {
    UserResponse register(RegisterUserRequest request) throws KingBankException;
}
