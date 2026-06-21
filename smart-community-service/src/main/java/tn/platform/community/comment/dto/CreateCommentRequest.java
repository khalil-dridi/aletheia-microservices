package tn.platform.community.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {

    @NotNull
    private Long postId;

    @NotNull
    private Long authorId;

    @NotBlank
    private String content;
}