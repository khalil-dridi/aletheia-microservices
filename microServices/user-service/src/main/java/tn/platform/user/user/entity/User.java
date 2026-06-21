package tn.platform.user.user.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = true;
    private boolean deleted = false;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;

    private String photoUrl;
    private String bio;

    private LocalDateTime createdAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.photoUrl == null || this.photoUrl.isEmpty()) {
            this.photoUrl = "https://cdn-icons-png.flaticon.com/512/149/149071.png";
        }
    }


}
