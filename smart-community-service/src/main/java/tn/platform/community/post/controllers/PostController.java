package tn.platform.community.post.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import tn.platform.community.post.dto.*;
import tn.platform.community.post.services.PostService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Posts API", description = "Operations related to community posts")
public class PostController {

    private final PostService service;

    /* ================= Pagination ================= */
    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 50) size = 10;

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }

    /* ================= CREATE with images ================= */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> create(
            @RequestParam Long authorId,
            @RequestParam String content,
            @RequestPart(required = false) MultipartFile[] images,
            UriComponentsBuilder uriBuilder) {

        log.info("API create post authorId={}", authorId);

        PostResponse created = service.createWithImages(authorId, content, images);

        var location = uriBuilder
                .path("/api/posts/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /* ================= UPDATE with images ================= */
    @Operation(summary = "Update post (with optional images)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @RequestPart(required = false) String content,
            @RequestPart(required = false) MultipartFile[] images) {

        log.info("API update post id={}", id);

        return ResponseEntity.ok(service.updateWithImages(id, content, images));
    }

    /* ================= GET ================= */
    @Operation(summary = "Get all posts")
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Get post by id")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /* ================= DELETE ================= */
    @Operation(summary = "Delete post (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("API delete post id={}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ================= SEARCH ================= */
    @Operation(summary = "Search posts")
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = buildPageable(page, size, "createdAt", "desc");
        return ResponseEntity.ok(service.search(q, pageable));
    }

    @Operation(summary = "Posts by author")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<PostResponse>> getByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = buildPageable(page, size, "createdAt", "desc");
        return ResponseEntity.ok(service.getByAuthor(authorId, pageable));
    }
}