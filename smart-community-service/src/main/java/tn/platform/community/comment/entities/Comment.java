package tn.platform.community.comment.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Comment entity for SmartCommunityService.
 * Represents a comment on a community post.
 */
@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id"),
                @Index(name = "idx_comment_author", columnList = "author_id"),
                @Index(name = "idx_comment_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of related post (from Post module)
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * ID of user from UserService
     */
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    /**
     * Comment content text
     */
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    /**
     * Creation timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Soft delete flag
     */
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Optimistic locking
     */
    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
