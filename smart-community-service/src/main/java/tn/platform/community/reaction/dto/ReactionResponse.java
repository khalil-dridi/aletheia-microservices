package tn.platform.community.reaction.dto;


import lombok.*;
import tn.platform.community.reaction.entity.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionResponse {
    private Long id;
    private Long userId;
    private Long targetId;
    private TargetType targetType;
    private ReactionType type;
}
