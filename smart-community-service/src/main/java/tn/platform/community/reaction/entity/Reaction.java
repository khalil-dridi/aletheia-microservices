package tn.platform.community.reaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Reaction entity (Like / Love / etc.)
 * Can target Post or Comment.
 */
@Entity
@Table(
        name = "reactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uniq_user_target",
                columnNames = {"user_id", "target_id", "target_type"}
        ),
        indexes = {
                @Index(name = "idx_reaction_target", columnList = "target_id"),
                @Index(name = "idx_reaction_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User from UserService
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ID of Post or Comment
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    // POST or COMMENT
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    // LIKE, LOVE, etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReactionType type;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
