package tn.platform.community.reaction.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.platform.community.reaction.dto.*;
import tn.platform.community.reaction.entity.*;
import tn.platform.community.reaction.repository.ReactionRepository;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository repo;

    @Override
    public ReactionResponse react(CreateReactionRequest request) {

        Reaction reaction = repo
                .findByUserIdAndTargetIdAndTargetType(
                        request.getUserId(),
                        request.getTargetId(),
                        request.getTargetType())
                .orElse(
                        Reaction.builder()
                                .userId(request.getUserId())
                                .targetId(request.getTargetId())
                                .targetType(request.getTargetType())
                                .type(request.getType())
                                .build()
                );

        reaction.setType(request.getType());

        Reaction saved = repo.save(reaction);

        return ReactionResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .targetId(saved.getTargetId())
                .targetType(saved.getTargetType())
                .type(saved.getType())
                .build();
    }

    @Override
    public void removeReaction(Long userId, Long targetId, String targetType) {
        repo.findByUserIdAndTargetIdAndTargetType(
                        userId,
                        targetId,
                        TargetType.valueOf(targetType))
                .ifPresent(repo::delete);
    }

    @Override
    public long countReactions(Long targetId, String targetType, String type) {
        return repo.countByTargetIdAndTargetTypeAndType(
                targetId,
                TargetType.valueOf(targetType),
                ReactionType.valueOf(type)
        );
    }
}
