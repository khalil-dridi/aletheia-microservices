package tn.platform.user.instructorrequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import tn.platform.user.instructorrequest.dto.InstructorRequestResponse;
import tn.platform.user.instructorrequest.entity.InstructorRequest;
import tn.platform.user.instructorrequest.entity.RequestStatus;
import tn.platform.user.instructorrequest.repository.InstructorRequestRepository;
import tn.platform.user.instructorrequest.service.InstructorRequestServiceImpl;
import tn.platform.user.media.service.CloudinaryService;
import tn.platform.user.user.entity.Role;
import tn.platform.user.user.entity.User;
import tn.platform.user.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class instructorrequestTest {

    @Mock
    private InstructorRequestRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private InstructorRequestServiceImpl service;

    @Captor
    ArgumentCaptor<InstructorRequest> requestCaptor;

    @Test
    void createRequest_success() {
        // arrange
        String email = "learner@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setRole(Role.LEARNER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(repository.existsByUserAndStatus(user, RequestStatus.PENDING)).thenReturn(false);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );

        String uploadedUrl = "https://res.cloudinary.com/demo/raw/upload/v123456/cv.pdf";
        when(cloudinaryService.uploadFile(any())).thenReturn(uploadedUrl);

        when(repository.save(any(InstructorRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        InstructorRequestResponse response = service.createRequest(email, file, "motivation text");

        // assert
        assertNotNull(response);
        assertEquals(uploadedUrl, response.getCvUrl());
        assertEquals(RequestStatus.PENDING, response.getStatus());
        verify(repository).save(requestCaptor.capture());
        InstructorRequest saved = requestCaptor.getValue();
        assertEquals(uploadedUrl, saved.getCvUrl());
        assertEquals(RequestStatus.PENDING, saved.getStatus());
        assertEquals(user, saved.getUser());
    }

    @Test
    void createRequest_notLearner_throws() {
        String email = "admin@example.com";
        User user = new User();
        user.setId(2L);
        user.setEmail(email);
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "dummy".getBytes()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.createRequest(email, file, "motivation")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("only learners"));
        verify(repository, never()).save(any());
    }

    @Test
    void createRequest_existingPending_throws() {
        String email = "learner2@example.com";
        User user = new User();
        user.setId(3L);
        user.setEmail(email);
        user.setRole(Role.LEARNER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(repository.existsByUserAndStatus(user, RequestStatus.PENDING)).thenReturn(true);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "dummy".getBytes()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.createRequest(email, file, "motivation")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("pending"));
        verify(repository, never()).save(any());
    }
}

