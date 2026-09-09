package dev.fantasma.kingbank.service;

import dev.fantasma.kingbank.mapper.UserMapper;
import dev.fantasma.kingbank.data.models.Authority;
import dev.fantasma.kingbank.data.models.User;
import dev.fantasma.kingbank.data.repositories.UserRepository;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import dev.fantasma.kingbank.exception.InvalidRequestException;
import dev.fantasma.kingbank.exception.KingBankException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static dev.fantasma.kingbank.utils.Validator.validate;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse registerUser(RegisterUserRequest request) throws KingBankException {
        return createUser(request, Set.of(Authority.ROLE_CUSTOMER));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse registerTeller(RegisterUserRequest request) throws KingBankException {
        return createUser(request, Set.of(Authority.ROLE_TELLER));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse registerAdmin(RegisterUserRequest request) throws KingBankException {
        return createUser(request, Set.of(Authority.ROLE_ADMIN));
    }

    private UserResponse createUser(RegisterUserRequest request, Set<Authority> authorities) throws KingBankException {
        validate(request);
        request.setUsername(request.getUsername().trim().toLowerCase());
        request.setFirstName(request.getFirstName().trim());
        request.setLastName(request.getLastName().trim());
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        if (userRepository.existsByUsername(request.getUsername()))
            throw new InvalidRequestException("Username already exists.");

        User user = userMapper.toEntity(request);
        user.setAuthorities(authorities);
        return userMapper.toResponse(userRepository.save(user));
    }
}
