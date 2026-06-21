package tn.platform.community.comment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.platform.community.comment.dto.*;
import tn.platform.community.comment.entities.Comment;
import tn.platform.community.comment.repositories.CommentRepository;
import tn.platform.community.config.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repo;

    /* ===========================
       Mapper
    =========================== */
    private CommentResponse map(Comment c){
        return CommentResponse.builder()
                .id(c.getId())
                .postId(c.getPostId())
                .authorId(c.getAuthorId())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    /* ===========================
       CREATE
    =========================== */
    @Override
    @Transactional
    public CommentResponse create(CreateCommentRequest request) {

        if (request.getContent().length() > 500)
            throw new IllegalArgumentException("Comment too long");

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .build();

        Comment saved = repo.save(comment);

        log.info("Comment created id={} postId={} authorId={}",
                saved.getId(),
                saved.getPostId(),
                saved.getAuthorId());

        return map(saved);
    }

    /* ===========================
       GET BY POST
    =========================== */
    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getByPost(Long postId, Pageable pageable) {
        return repo.findByPostIdAndDeletedFalse(postId, pageable)
                .map(this::map);
    }

    /* ===========================
       UPDATE
    =========================== */
    @Override
    @Transactional
    public CommentResponse update(Long id, UpdateCommentRequest request) {

        Comment comment = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found id=" + id));

        if (request.getContent().length() > 500)
            throw new IllegalArgumentException("Comment too long");

        comment.setContent(request.getContent());

        Comment saved = repo.save(comment);

        log.info("Comment updated id={}", saved.getId());

        return map(saved);
    }

    /* ===========================
       DELETE (Soft Delete)
    =========================== */
    @Override
    @Transactional
    public void delete(Long id) {

        Comment comment = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found id=" + id));

        comment.setDeleted(true);
        repo.save(comment);

        log.info("Comment soft-deleted id={}", id);
    }

    /* ===========================
       FUTURE FEATURES
    =========================== */

    public long countByPost(Long postId){
        return repo.countByPostIdAndDeletedFalse(postId);
    }

    public long countByAuthor(Long authorId){
        return repo.countByAuthorIdAndDeletedFalse(authorId);
    }

    public Page<CommentResponse> search(String q, Pageable pageable){
        return repo.search(q, pageable).map(this::map);
    }
}
