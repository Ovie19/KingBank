package dev.fantasma.kingbank.service;

import dev.fantasma.kingbank.data.repositories.UserRepository;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.CsvSources;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void setUp() {
        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("ben999");
        registerUserRequest.setPassword("a23@#679aksh");
        registerUserRequest.setName("Franklin Saint");
    }

    @Test
    void registerUserRequestIsNull_registerUserThrowsException() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"    "})
    void nullOrEmptyUsername_registerUserThrowsException(String username) {
        registerUserRequest.setUsername(username);
        InvalidRequestException ex = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "'wi', 'Username must be at least 4 characters'",
            "'  wim', 'Username must be at least 4 characters'",
            "' wim  ', 'Username must be at least 4 characters'",
            "'$wim  ', 'Username must start with a letter'",
            "'.wim  ', 'Username must start with a letter'",
            "'0wim  ', 'Username must start with a letter'",
            "'_wim  ', 'Username must start with a letter'",
            "'wimp 001', 'Username cannot contain spaces'",
            "'w 007', 'Username cannot contain spaces'",
    })
    void invalidUsername_registerUserThrowsException(String username, String expectedMessage) {
        registerUserRequest.setUsername(username);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }
}