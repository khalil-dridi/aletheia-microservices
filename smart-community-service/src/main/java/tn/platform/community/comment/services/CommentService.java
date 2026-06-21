package tn.platform.community.comment.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.platform.community.comment.dto.*;

public interface CommentService {

    /* ===========================
       CREATE
    =========================== */
    CommentResponse create(CreateCommentRequest request);

    /* ===========================
       READ
    =========================== */
    Page<CommentResponse> getByPost(Long postId, Pageable pageable);

    /* ===========================
       UPDATE
    =========================== */
    CommentResponse update(Long id, UpdateCommentRequest request);

    /* ===========================
       DELETE
    =========================== */
    void delete(Long id);

    /* ===========================
       ANALYTICS
    =========================== */
    long countByPost(Long postId);

    long countByAuthor(Long authorId);

    /* ===========================
       SEARCH
    =========================== */
    Page<CommentResponse> search(String query, Pageable pageable);
}
