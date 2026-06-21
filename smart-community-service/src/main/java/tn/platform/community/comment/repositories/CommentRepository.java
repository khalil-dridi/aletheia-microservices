package tn.platform.community.comment.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import tn.platform.community.comment.entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /* ===========================
       BASIC QUERIES
    =========================== */

    Page<Comment> findByPostIdAndDeletedFalse(Long postId, Pageable pageable);

    Page<Comment> findByAuthorIdAndDeletedFalse(Long authorId, Pageable pageable);

    Optional<Comment> findByIdAndDeletedFalse(Long id);

    boolean existsByIdAndDeletedFalse(Long id);

    long countByPostIdAndDeletedFalse(Long postId);

    long countByAuthorIdAndDeletedFalse(Long authorId);

    List<Comment> findAllByPostIdAndDeletedFalse(Long postId);


    /* ===========================
       SEARCH
    =========================== */

    @Query("""
           SELECT c FROM Comment c
           WHERE c.deleted = false
           AND LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    Page<Comment> search(@Param("query") String query, Pageable pageable);


    /* ===========================
       RECENT COMMENTS
    =========================== */

    Page<Comment> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

}
