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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void setUp() {
        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("ben999");
        registerUserRequest.setPassword("a23@#679aksh");
        registerUserRequest.setFirstName("Franklin");
        registerUserRequest.setLastName("Saint");
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"pass", "passwor", "  pass"})
    void invalidPassword_registerUserThrowsException(String password) {
        registerUserRequest.setPassword(password);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );
        assertEquals("Password must be at least 8 characters", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void invalidFirstName_registerUserThrowsException(String firstName) {
        registerUserRequest.setFirstName(firstName);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );
        assertEquals("First name cannot be blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void invalidLastName_registerUserThrowsException(String lastName) {
        registerUserRequest.setLastName(lastName);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );
        assertEquals("Last name cannot be blank", exception.getMessage());
    }

    @Test
    void usernameAlreadyExist_registerWithUsernameThrowsException() {
        when(userRepository.existsByUsername(registerUserRequest.getUsername().trim().toLowerCase()))
                .thenReturn(true);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.register(registerUserRequest)
        );

        assertEquals("Username already exists.", exception.getMessage());
        verify(userRepository).existsByUsername(registerUserRequest.getUsername().trim().toLowerCase());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserSuccessfulTest() throws KingBankException {
        User mappedUser = new User();
        mappedUser.setUsername(registerUserRequest.getUsername().trim().toLowerCase());
        mappedUser.setName("Franklin Saint");

        User savedUser = new User();
        savedUser.setId("some-generated-id");
        savedUser.setUsername(registerUserRequest.getUsername().trim().toLowerCase());
        savedUser.setName("Franklin Saint");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId("some-generated-id");
        expectedResponse.setUsername(registerUserRequest.getUsername().trim().toLowerCase());

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.toEntity(registerUserRequest)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse response = userService.register(registerUserRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(registerUserRequest.getUsername().trim().toLowerCase(), response.getUsername());
    }
}