package tn.platform.community.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {

    @NotNull(message = "authorId is required")
    private Long authorId;

    @NotBlank(message = "content must    not be blank")
    private String content;
}
