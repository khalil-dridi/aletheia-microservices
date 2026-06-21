package tn.platform.community.comment.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tn.platform.community.comment.dto.*;
import tn.platform.community.comment.services.CommentService;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comments API", description = "Operations related to post comments")
public class CommentController {

    private final CommentService service;

    private Pageable buildPageable(int page, int size){
        if (page < 0) page = 0;
        if (size > 50) size = 50;
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    /* ===========================
       CREATE
    =========================== */
    @Operation(summary = "Create comment")
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @Valid @RequestBody CreateCommentRequest request,
            UriComponentsBuilder uriBuilder){

        log.info("API create comment postId={} authorId={}",
                request.getPostId(),
                request.getAuthorId());

        CommentResponse created = service.create(request);

        var location = uriBuilder
                .path("/api/comments/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /* ===========================
       GET COMMENTS BY POST
    =========================== */
    @Operation(summary = "Get comments of a post")
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponse>> getByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = buildPageable(page, size);
        return ResponseEntity.ok(service.getByPost(postId, pageable));
    }

    /* ===========================
       UPDATE
    =========================== */
    @Operation(summary = "Update comment")
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request){

        log.info("API update comment id={}", id);
        return ResponseEntity.ok(service.update(id, request));
    }

    /* ===========================
       DELETE
    =========================== */
    @Operation(summary = "Delete comment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        log.info("API delete comment id={}", id);
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    /* ===========================
       SEARCH
    =========================== */
    @Operation(summary = "Search comments")
    @GetMapping("/search")
    public ResponseEntity<Page<CommentResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = buildPageable(page, size);
        return ResponseEntity.ok(service.search(q, pageable));
    }

    /* ===========================
       COUNT
    =========================== */
    @Operation(summary = "Count comments for a post")
    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Long> countByPost(@PathVariable Long postId){
        return ResponseEntity.ok(service.countByPost(postId));
    }

    @Operation(summary = "Count comments by author")
    @GetMapping("/count/author/{authorId}")
    public ResponseEntity<Long> countByAuthor(@PathVariable Long authorId){
        return ResponseEntity.ok(service.countByAuthor(authorId));
    }
}
