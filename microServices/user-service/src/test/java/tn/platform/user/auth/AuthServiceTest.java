package tn.platform.user.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.platform.user.auth.dto.AuthResponse;
import tn.platform.user.auth.dto.LoginRequest;
import tn.platform.user.auth.dto.RegisterRequest;
import tn.platform.user.auth.service.AuthServiceImpl;
import tn.platform.user.security.jwt.JwtService;
import tn.platform.user.user.entity.Role;
import tn.platform.user.user.entity.User;
import tn.platform.user.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User persistedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setNom("Dupont");
        registerRequest.setPrenom("Jean");
        registerRequest.setEmail("jean.dupont@test.com");
        registerRequest.setPassword("secretPassword");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("jean.dupont@test.com");
        loginRequest.setPassword("secretPassword");

        persistedUser = User.builder()
                .id(42L)
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@test.com")
                .password("encoded-secret")
                .role(Role.LEARNER)
                .enabled(true)
                .deleted(false)
                .build();
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("creates user with LEARNER role and returns JWT payload")
        void success() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
            when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

            doAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(99L);
                return u;
            }).when(userRepository).save(any(User.class));

            AuthResponse response = authService.register(registerRequest);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getUserId()).isEqualTo(99L);
            assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
            assertThat(response.getNom()).isEqualTo(registerRequest.getNom());
            assertThat(response.getPrenom()).isEqualTo(registerRequest.getPrenom());
            assertThat(response.getRole()).isEqualTo(Role.LEARNER.name());

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User saved = userCaptor.getValue();
            assertThat(saved.getPassword()).isEqualTo("encoded-password");
            assertThat(saved.getRole()).isEqualTo(Role.LEARNER);
            assertThat(saved.isEnabled()).isTrue();

            verify(jwtService).generateToken(saved);
            verify(userRepository, never()).findByEmail(any());
        }

        @Test
        @DisplayName("throws when email is already registered")
        void emailAlreadyUsed() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));

            assertEquals("Email already used", ex.getMessage());
            verify(userRepository, never()).save(any());
            verify(jwtService, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("authenticates active user and returns JWT")
        void success() {
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(persistedUser));
            when(jwtService.generateToken(persistedUser)).thenReturn("login-jwt");

            AuthResponse response = authService.login(loginRequest);

            assertThat(response.getToken()).isEqualTo("login-jwt");
            assertThat(response.getUserId()).isEqualTo(persistedUser.getId());
            assertThat(response.getEmail()).isEqualTo(persistedUser.getEmail());
            assertThat(response.getRole()).isEqualTo(persistedUser.getRole().name());

            verify(authenticationManager).authenticate(
                    eq(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    ))
            );
            verify(jwtService).generateToken(persistedUser);
        }

        @Test
        @DisplayName("throws when user does not exist")
        void userNotFound() {
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

            assertEquals("User not found", ex.getMessage());
            verify(authenticationManager, never()).authenticate(any());
            verify(jwtService, never()).generateToken(any());
        }

        @Test
        @DisplayName("throws when account is deleted")
        void accountDeleted() {
            persistedUser.setDeleted(true);
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(persistedUser));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

            assertEquals("Account has been deleted", ex.getMessage());
            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("throws when account is disabled")
        void accountDisabled() {
            persistedUser.setEnabled(false);
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(persistedUser));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

            assertEquals("Account is disabled", ex.getMessage());
            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("propagates BadCredentialsException when password is wrong")
        void badCredentials() {
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(persistedUser));
            doThrow(new BadCredentialsException("Bad credentials"))
                    .when(authenticationManager).authenticate(any());

            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

            verify(jwtService, never()).generateToken(any());
        }
    }
}
