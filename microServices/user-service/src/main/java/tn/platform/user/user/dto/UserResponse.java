package tn.platform.user.user.dto;

import lombok.Builder;
import lombok.Data;
import tn.platform.user.user.entity.Role;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private String photoUrl;
    private String bio;
    private LocalDateTime createdAt;
    private boolean enabled;
}
