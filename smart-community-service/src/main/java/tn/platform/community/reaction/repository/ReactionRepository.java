package tn.platform.community.reaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.platform.community.reaction.entity.*;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByUserIdAndTargetIdAndTargetType(
            Long userId,
            Long targetId,
            TargetType targetType
    );

    long countByTargetIdAndTargetTypeAndType(
            Long targetId,
            TargetType targetType,
            ReactionType type
    );
}
