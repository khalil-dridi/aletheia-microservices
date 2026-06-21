package tn.platform.community.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostRequest {

    @NotBlank(message = "content must not be blank")
    private String content;
}
