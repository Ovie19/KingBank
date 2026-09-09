package dev.fantasma.kingbank.service;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static dev.fantasma.kingbank.data.models.Authority.*;
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

    private RegisterUserRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterUserRequest();
        request.setUsername("ben999");
        request.setPassword("a23@#679aksh");
        request.setFirstName("Franklin");
        request.setLastName("Saint");

        userRepository.deleteAll();
    }

    @Test
    void registerUserSuccessfullyTest() throws KingBankException {
        UserResponse userResponse = userService.registerUser(request);
        assertNotNull(userResponse);
        assertNotNull(userResponse.getId());

        User savedUser = userRepository.findByUsername(userResponse.getUsername()).orElseThrow();
        assertEquals("ben999", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("a23@#679aksh", savedUser.getPassword()));
        assertEquals("Franklin Saint", savedUser.getName());
        assertEquals(Set.of(ROLE_CUSTOMER), savedUser.getAuthorities());
    }

    @Test
    void usernameExist_registerUserWithSameUsernameThrowsException() throws KingBankException {
        userService.registerUser(request);
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(request)
        );
        assertEquals("Username already exists.", exception.getMessage());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customer_cannotRegisterTellerTest() {
        assertThrows(
                AccessDeniedException.class,
                () -> userService.registerTeller(request)
        );
    }

    @Test
    @WithMockUser(roles = "TELLER")
    void teller_cannotRegisterTellerTest() {
        assertThrows(
                AccessDeniedException.class,
                () -> userService.registerTeller(request)
        );
    }

    @Test
    void anonymous_cannotRegisterTellerTest() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> userService.registerTeller(request)
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canRegisterTellerTest() throws KingBankException {
        UserResponse userResponse = userService.registerTeller(request);
        assertNotNull(userResponse);
        assertNotNull(userResponse.getId());

        User savedUser = userRepository.findByUsername(userResponse.getUsername()).orElseThrow();
        assertEquals("ben999", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("a23@#679aksh", savedUser.getPassword()));
        assertEquals("Franklin Saint", savedUser.getName());
        assertEquals(Set.of(ROLE_TELLER), savedUser.getAuthorities());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customer_cannotRegisterAdminTest() {
        assertThrows(
                AccessDeniedException.class,
                () -> userService.registerAdmin(request)
        );
    }

    @Test
    @WithMockUser(roles = "TELLER")
    void teller_cannotRegisterAdminTest() {
        assertThrows(
                AccessDeniedException.class,
                () -> userService.registerAdmin(request)
        );
    }

    @Test
    void anonymous_cannotRegisterAdminTest() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> userService.registerAdmin(request)
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canRegisterAdminTest() throws KingBankException {
        UserResponse userResponse = userService.registerAdmin(request);
        assertNotNull(userResponse);
        assertNotNull(userResponse.getId());

        User savedUser = userRepository.findByUsername(userResponse.getUsername()).orElseThrow();
        assertEquals("ben999", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("a23@#679aksh", savedUser.getPassword()));
        assertEquals("Franklin Saint", savedUser.getName());
        assertEquals(Set.of(ROLE_ADMIN), savedUser.getAuthorities());
    }
}
