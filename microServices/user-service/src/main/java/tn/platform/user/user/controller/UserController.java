package tn.platform.user.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.platform.user.user.dto.ChangePasswordRequest;
import tn.platform.user.user.dto.UpdateUserRequest;
import tn.platform.user.user.dto.UserResponse;
import tn.platform.user.user.entity.Role;
import tn.platform.user.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.io.IOException;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(userService.updateCurrentUser(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);
        return ResponseEntity.ok("Password updated");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe() {

        userService.deleteCurrentUser();
        return ResponseEntity.ok("Account deleted");
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            Pageable pageable,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "role", required = false) String roleParam) {

        Role roleFilter = null;
        if (StringUtils.hasText(roleParam)) {
            try {
                roleFilter = Role.valueOf(roleParam.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid role: " + roleParam);
            }
        }

        return ResponseEntity.ok(userService.getAllUsers(pageable, roleFilter, q));
    }

    @PostMapping("/me/photo")
    public ResponseEntity<UserResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(userService.uploadPhoto(file));
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                userService.getUserByIdForMicroservice(id)
        );
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok("User status updated");
    }


}
