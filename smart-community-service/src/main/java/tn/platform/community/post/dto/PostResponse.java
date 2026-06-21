    package tn.platform.community.post.dto;

    import lombok.*;
    import java.time.LocalDateTime;
    import java.util.List;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class PostResponse {
        private Long id;
        private Long authorId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        @Builder.Default
        private List<String> imageUrls = new java.util.ArrayList<>();

    }
