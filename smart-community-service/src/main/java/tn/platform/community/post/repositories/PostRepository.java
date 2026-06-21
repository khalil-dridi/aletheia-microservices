package tn.platform.community.post.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import tn.platform.community.post.entities.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Get all non-deleted posts
     */
    Page<Post> findAllByDeletedFalse(Pageable pageable);

    /**
     * Search posts by content keyword
     */
    @Query("""
           SELECT p FROM Post p
           WHERE p.deleted = false
           AND LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    Page<Post> search(@Param("query") String query, Pageable pageable);

    /**
     * Posts by author
     */
    Page<Post> findByAuthorIdAndDeletedFalse(Long authorId, Pageable pageable);

    /**
     * Get single post
     */
    Optional<Post> findByIdAndDeletedFalse(Long id);

    /**
     * Check existence
     */
    boolean existsByIdAndDeletedFalse(Long id);

    /**
     * Count posts by author (useful for reputation system)
     */
    long countByAuthorIdAndDeletedFalse(Long authorId);
}
