package dev.fantasma.kingbank.service;

import dev.fantasma.kingbank.Mapper.UserMapper;
import dev.fantasma.kingbank.data.models.User;
import dev.fantasma.kingbank.data.repositories.UserRepository;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import dev.fantasma.kingbank.exception.InvalidRequestException;
import dev.fantasma.kingbank.exception.KingBankException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static dev.fantasma.kingbank.utils.Validator.validate;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(RegisterUserRequest request) throws KingBankException {
        validate(request);
        request.setUsername(request.getUsername().trim().toLowerCase());
        request.setFirstName(request.getFirstName().trim());
        request.setLastName(request.getLastName().trim());
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        if (userRepository.existsByUsername(request.getUsername()))
            throw new InvalidRequestException("Username already exists.");

        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }
}
