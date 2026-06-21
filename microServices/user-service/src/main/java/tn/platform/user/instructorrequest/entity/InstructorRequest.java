package tn.platform.user.instructorrequest.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.platform.user.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "instructor_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_instructor_request_user", columnNames = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String cvUrl;

    @Column(nullable = false)
    private Double score = 0.0;

    @Column(length = 1000)
    private String aiFeedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
        if (this.score == null) {
            this.score = 0.0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}