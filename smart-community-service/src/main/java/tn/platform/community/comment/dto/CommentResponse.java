package tn.platform.community.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
