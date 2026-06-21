package tn.platform.user.instructorrequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.platform.user.instructorrequest.dto.InstructorRequestResponse;
import tn.platform.user.instructorrequest.entity.InstructorRequest;
import tn.platform.user.instructorrequest.entity.RequestStatus;
import tn.platform.user.instructorrequest.repository.InstructorRequestRepository;
import tn.platform.user.media.service.CloudinaryService;
import tn.platform.user.notifications.NotificationRequest;
import tn.platform.user.user.entity.Role;
import tn.platform.user.user.entity.User;
import tn.platform.user.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorRequestServiceImpl implements InstructorRequestService {
    private final InstructorRequestRepository repository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final RestTemplate restTemplate;
    @Override
    public InstructorRequestResponse createRequest(
            String email,
            MultipartFile file,
            String motivation) {

        // 1️⃣ récupérer user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ vérifier role (seul LEARNER autorisé)
        if (user.getRole() != Role.LEARNER) {
            throw new RuntimeException("Only learners can request instructor role");
        }

        // 3️⃣ vérifier si déjà une demande existante (option pro)
        boolean exists = repository.existsByUserAndStatus(user, RequestStatus.PENDING);
        if (exists) {
            throw new RuntimeException("You already have a pending request");
        }

        // 4️⃣ upload CV
        String cvUrl = uploadCv(file);

        // 5️⃣ créer entity
        InstructorRequest request = InstructorRequest.builder()
                .user(user)
                .cvUrl(cvUrl)
                .status(RequestStatus.PENDING)
                .build();

        // 6️⃣ sauvegarder
        repository.save(request);

        // 7️⃣ retourner réponse (temporaire simple)
        return InstructorRequestResponse.builder()
                .id(request.getId())
                .userId(user.getId())
                .userName(user.getNom() + " " + user.getPrenom())
                .cvUrl(request.getCvUrl())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }

    private String uploadCv(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getContentType() == null ||
                !file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Max file size is 5MB");
        }

        return cloudinaryService.uploadFile(file);
    }



    @Override
    public List<InstructorRequestResponse> getAllRequests() {

        return repository.findAll()
                .stream()
                .map(req -> InstructorRequestResponse.builder()
                        .id(req.getId())
                        .userId(req.getUser().getId())
                        .userName(req.getUser().getNom() + " " + req.getUser().getPrenom())
                        .cvUrl(req.getCvUrl())
                        .score(req.getScore())
                        .aiFeedback(req.getAiFeedback())
                        .status(req.getStatus())
                        .createdAt(req.getCreatedAt())
                        .updatedAt(req.getUpdatedAt())
                        .build()
                )
                .toList();
    }

    @Override
    public void approveRequest(Long requestId) {

        InstructorRequest req = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        User user = req.getUser();

        user.setRole(Role.INSTRUCTOR);
        req.setStatus(RequestStatus.APPROVED);

        userRepository.save(user);
        repository.save(req);

        sendNotification(
                user.getId().toString(),
                "Votre demande Instructor a été acceptée."
        );
    }

    @Override
    public void rejectRequest(Long requestId) {

        InstructorRequest req = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        req.setStatus(RequestStatus.REJECTED);
        repository.save(req);

        sendNotification(
                req.getUser().getId().toString(),
                "Votre demande Instructor a été refusée."
        );
    }

    /*private void sendNotification(String userId, String message) {

        try {

            System.out.println("=== START NOTIFICATION ===");

            NotificationRequest notification =
                    new NotificationRequest(userId, message);

            System.out.println("DTO CREATED");

            String response = restTemplate.postForObject(
                    "http://localhost:8080/notifications",
                    notification,
                    String.class
            );

            System.out.println("RESPONSE = " + response);

        } catch (Exception e) {

            System.out.println("NOTIFICATION ERROR");

            e.printStackTrace();
        }
    }
*/


    private void sendNotification(String userId, String message) {

        try {

            System.out.println("===== SEND NOTIFICATION =====");

            NotificationRequest notification =
                    new NotificationRequest(userId, message);

            System.out.println("UserId = " + userId);
            System.out.println("Message = " + message);

            String response = restTemplate.postForObject(
                    "http://notification-service:3000/notifications",
                    notification,
                    String.class
            );

            System.out.println("RESPONSE = " + response);
            System.out.println("===== NOTIFICATION SENT =====");

        } catch (Exception e) {

            System.out.println("===== NOTIFICATION ERROR =====");
            e.printStackTrace();
        }
    }



}