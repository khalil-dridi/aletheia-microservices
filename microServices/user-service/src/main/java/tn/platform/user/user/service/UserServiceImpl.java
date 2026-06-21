package tn.platform.user.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.platform.user.media.service.CloudinaryService;
import tn.platform.user.user.dto.ChangePasswordRequest;
import tn.platform.user.user.dto.UpdateUserRequest;
import tn.platform.user.user.dto.UserResponse;
import tn.platform.user.user.entity.Role;
import tn.platform.user.user.entity.User;
import tn.platform.user.user.exception.UserNotFoundException;
import tn.platform.user.user.mapper.UserMapper;
import tn.platform.user.user.repository.UserRepository;
import tn.platform.user.user.repository.UserSpecifications;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements  UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;



    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        User user = getById(id);
        user.setDeleted(true); // soft delete
        userRepository.save(user);
    }
    @Override
    public UserResponse getCurrentUser() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }
    @Override
    public UserResponse updateCurrentUser(UpdateUserRequest request) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getNom() != null)
            user.setNom(request.getNom());

        if (request.getPrenom() != null)
            user.setPrenom(request.getPrenom());


        if (request.getPhotoUrl() != null)
            user.setPhotoUrl(request.getPhotoUrl());

        if (request.getBio() != null)
            user.setBio(request.getBio());

        userRepository.save(user);

        return userMapper.toResponse(user);
    }


    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteCurrentUser() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setDeleted(true);
        user.setEnabled(false);

        userRepository.save(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, Role role, String search) {
        Specification<User> spec = Specification
                .where(UserSpecifications.notDeleted())
                .and(UserSpecifications.hasRole(role))
                .and(UserSpecifications.matchesSearch(search));
        return userRepository.findAll(spec, pageable).map(userMapper::toResponse);
    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public UserResponse uploadPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String url = cloudinaryService.uploadImage(file);

        user.setPhotoUrl(url);
        userRepository.save(user);

        return userMapper.toResponse(user);
    }
    @Override
    public UserResponse getUserByIdForMicroservice(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }

    @Override
    public void softDeleteUser(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEnabled(!user.isEnabled()); // 🔁 toggle
        userRepository.save(user);
    }







}
