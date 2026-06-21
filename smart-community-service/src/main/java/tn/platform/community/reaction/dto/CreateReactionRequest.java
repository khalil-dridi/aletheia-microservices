package tn.platform.community.reaction.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.platform.community.reaction.entity.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReactionRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long targetId;

    @NotNull
    private TargetType targetType;

    @NotNull
    private ReactionType type;
}

