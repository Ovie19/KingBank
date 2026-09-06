package dev.fantasma.kingbank.service;

import dev.fantasma.kingbank.data.repositories.UserRepository;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import dev.fantasma.kingbank.exception.KingBankException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static dev.fantasma.kingbank.utils.Validator.validate;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse register(RegisterUserRequest request) throws KingBankException {
        validate(request);
        return null;
    }
}
