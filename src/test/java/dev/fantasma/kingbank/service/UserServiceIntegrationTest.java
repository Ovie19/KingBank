package dev.fantasma.kingbank.service;

import dev.fantasma.kingbank.Mapper.UserMapper;
import dev.fantasma.kingbank.data.models.User;
import dev.fantasma.kingbank.data.repositories.UserRepository;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import dev.fantasma.kingbank.exception.InvalidRequestException;
import dev.fantasma.kingbank.exception.KingBankException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void setUp() {
        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("ben999");
        registerUserRequest.setPassword("a23@#679aksh");
        registerUserRequest.setFirstName("Franklin");
        registerUserRequest.setLastName("Saint");

        userRepository.deleteAll();
    }

    @Test
    void registerUserSuccessfullyTest() throws KingBankException {
        UserResponse userResponse = userService.register(registerUserRequest);
        assertNotNull(userResponse);
        assertNotNull(userResponse.getId());

        User savedUser = userRepository.findByUsername(userResponse.getUsername()).orElseThrow();
        assertEquals("ben999", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("a23@#679aksh", savedUser.getPassword()));
        assertEquals("Franklin Saint", savedUser.getName());
    }

    @Test
    void usernameExist_registerWithSameUsernameThrowsException() throws KingBankException {
        userService.register(registerUserRequest);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );
        assertEquals("Username already exists.", exception.getMessage());
    }
}
