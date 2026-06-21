package tn.platform.user.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.platform.user.media.service.CloudinaryService;
import tn.platform.user.user.dto.ChangePasswordRequest;
import tn.platform.user.user.dto.UpdateUserRequest;
import tn.platform.user.user.dto.UserResponse;
import tn.platform.user.user.entity.User;
import tn.platform.user.user.exception.UserNotFoundException;
import tn.platform.user.user.mapper.UserMapper;
import tn.platform.user.user.repository.UserRepository;
import tn.platform.user.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = mock(Authentication.class);
        // Mark stubbings lenient to avoid unnecessary stubbing failures in tests that don't use all stubs
        org.mockito.Mockito.lenient().when(auth.getName()).thenReturn("tester@example.com");
        org.mockito.Mockito.lenient().when(auth.isAuthenticated()).thenReturn(true);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void getCurrentUser_success() {
        User u = new User();
        u.setEmail("tester@example.com");
        when(userRepository.findByEmail("tester@example.com")).thenReturn(Optional.of(u));
        when(userMapper.toResponse(u)).thenReturn(mock(UserResponse.class));

        UserResponse resp = userService.getCurrentUser();
        assertNotNull(resp);
        verify(userRepository).findByEmail("tester@example.com");
    }

    @Test
    void getCurrentUser_userNotFound_throws() {
        when(userRepository.findByEmail("tester@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
    }

    @Test
    void updateCurrentUser_success() {
        User u = new User();
        u.setEmail("tester@example.com");
        when(userRepository.findByEmail("tester@example.com")).thenReturn(Optional.of(u));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any())).thenReturn(mock(UserResponse.class));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setNom("NomTest");
        req.setPrenom("PrenomTest");
        req.setBio("bio");

        UserResponse resp = userService.updateCurrentUser(req);
        assertNotNull(resp);
        verify(userRepository).save(u);
        assertEquals("NomTest", u.getNom());
        assertEquals("PrenomTest", u.getPrenom());
        assertEquals("bio", u.getBio());
    }

    @Test
    void changePassword_success() {
        User u = new User();
        u.setEmail("tester@example.com");
        u.setPassword("encodedOld");

        when(userRepository.findByEmail("tester@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("oldPass", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("oldPass");
        req.setNewPassword("newPass");

        userService.changePassword(req);

        verify(userRepository).save(u);
        assertEquals("encodedNew", u.getPassword());
    }

    @Test
    void changePassword_wrongOld_throws() {
        User u = new User();
        u.setEmail("tester@example.com");
        u.setPassword("encodedOld");

        when(userRepository.findByEmail("tester@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("badOld", "encodedOld")).thenReturn(false);

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("badOld");
        req.setNewPassword("newPass");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.changePassword(req));
        assertTrue(ex.getMessage().toLowerCase().contains("old password incorrect"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void uploadPhoto_success() {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "photo.jpg",
                "image/jpeg",
                "data".getBytes()
        );

        User u = new User();
        u.setEmail("tester@example.com");

        when(userRepository.findByEmail("tester@example.com")).thenReturn(Optional.of(u));
        when(cloudinaryService.uploadImage(any())).thenReturn("https://res.cloudinary.com/demo/image/upload/v123/photo.jpg");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any())).thenReturn(mock(UserResponse.class));

        UserResponse resp = userService.uploadPhoto(file);
        assertNotNull(resp);
        verify(cloudinaryService).uploadImage(file);
        verify(userRepository).save(u);
        assertEquals("https://res.cloudinary.com/demo/image/upload/v123/photo.jpg", u.getPhotoUrl());
    }
}
