package tn.platform.community.reaction.service;

import tn.platform.community.reaction.dto.*;

public interface ReactionService {

    ReactionResponse react(CreateReactionRequest request);

    void removeReaction(Long userId, Long targetId, String targetType);

    long countReactions(Long targetId, String targetType, String type);
}