package tn.platform.community.post.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.platform.community.post.dto.*;
import tn.platform.community.post.entities.Post;
import tn.platform.community.post.repositories.PostRepository;
import tn.platform.community.config.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository repo;
    private final CloudinaryService cloudinaryService; // injection du service Cloudinary

    // Permis types et limites (Java 8 compatible)
    private static final Set<String> ALLOWED_CONTENT_TYPES =
            new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));
    private static final int MAX_IMAGES = 10;
    private static final long MAX_BYTES = 5L * 1024L * 1024L; // 5 MB
    private static final int MAX_CONTENT_LENGTH = 1000;

    /* ===========================
       Mapper
    =========================== */
    private PostResponse map(Post p) {
        return PostResponse.builder()
                .id(p.getId())
                .authorId(p.getAuthorId())
                .content(p.getContent())
                .imageUrls(p.getImageUrls())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    /* ===========================
       CREATE (legacy, JSON body)
    =========================== */
    @Override
    @Transactional
    public PostResponse create(CreatePostRequest request) {

        if (request.getContent() == null || request.getContent().isBlank())
            throw new IllegalArgumentException("content must not be blank");

        if (request.getContent().length() > MAX_CONTENT_LENGTH)
            throw new IllegalArgumentException("Post content too long");

        Post post = Post.builder()
                .authorId(request.getAuthorId())
                .content(request.getContent().trim())
                .deleted(false)
                .build();

        Post saved = repo.save(post);

        log.info("Post created id={} authorId={}", saved.getId(), saved.getAuthorId());

        return map(saved);
    }

    /* ===========================
       CREATE with images (multipart/form-data)
       — use this for Cloudinary uploads
    =========================== */
    @Transactional
    public PostResponse createWithImages(Long authorId,
                                         String content,
                                         MultipartFile[] images) {

        if (content == null || content.isBlank())
            throw new IllegalArgumentException("content must not be blank");

        content = content.trim();
        if (content.length() > MAX_CONTENT_LENGTH)
            throw new IllegalArgumentException("Post content too long (max " + MAX_CONTENT_LENGTH + " chars)");

        List<String> imageUrls = new ArrayList<>();

        if (images != null && images.length > 0) {

            if (images.length > MAX_IMAGES)
                throw new IllegalArgumentException("Too many images. Max = " + MAX_IMAGES);

            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) continue;

                // type check
                String contentType = file.getContentType();
                if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
                    throw new IllegalArgumentException("Unsupported file type: " + file.getOriginalFilename());
                }

                // size check
                if (file.getSize() > MAX_BYTES) {
                    throw new IllegalArgumentException("File too large: " + file.getOriginalFilename());
                }

                try {
                    String url = cloudinaryService.uploadFile(file);
                    if (url != null) imageUrls.add(url);
                } catch (RuntimeException ex) {
                    log.error("Failed to upload image {} : {}", file.getOriginalFilename(), ex.getMessage());
                    throw new RuntimeException("Image upload failed for " + file.getOriginalFilename());
                }
            }
        }

        Post post = Post.builder()
                .authorId(authorId)
                .content(content)
                .imageUrls(imageUrls)
                .deleted(false)
                .build();

        Post saved = repo.save(post);

        log.info("Post created (with images) id={} authorId={} images={}",
                saved.getId(),
                saved.getAuthorId(),
                saved.getImageUrls() == null ? 0 : saved.getImageUrls().size());

        return map(saved);
    }

    /* ===========================
       GET ALL
    =========================== */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getAll(Pageable pageable) {
        return repo.findAllByDeletedFalse(pageable)
                .map(this::map);
    }

    /* ===========================
       GET BY ID
    =========================== */
    @Override
    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        Post post = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found id=" + id));

        return map(post);
    }

    /* ===========================
       UPDATE
    =========================== */
    @Override
    @Transactional
    public PostResponse updateWithImages(Long id,
                                         String content,
                                         MultipartFile[] images) {

        Post post = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found id=" + id));

        // Update content
        if (content != null && !content.isBlank()) {
            if (content.length() > MAX_CONTENT_LENGTH)
                throw new IllegalArgumentException("Post content too long");
            post.setContent(content.trim());
        }

        // Update images
        if (images != null && images.length > 0) {

            if (images.length > MAX_IMAGES)
                throw new IllegalArgumentException("Too many images. Max = " + MAX_IMAGES);

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) continue;

                String contentType = file.getContentType();
                if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase()))
                    throw new IllegalArgumentException("Unsupported file type: " + file.getOriginalFilename());

                if (file.getSize() > MAX_BYTES)
                    throw new IllegalArgumentException("File too large: " + file.getOriginalFilename());

                String url = cloudinaryService.uploadFile(file);
                imageUrls.add(url);
            }

            post.setImageUrls(imageUrls); // remplace images
        }

        Post saved = repo.save(post);
        log.info("Post updated id={} images={}", saved.getId(),
                saved.getImageUrls() == null ? 0 : saved.getImageUrls().size());

        return map(saved);
    }

    /* ===========================
       DELETE (Soft Delete)
    =========================== */
    @Override
    @Transactional
    public void delete(Long id) {

        Post post = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found id=" + id));

        post.setDeleted(true);
        repo.save(post);

        log.info("Post soft-deleted id={}", id);
    }

    /* ===========================
       SEARCH
    =========================== */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> search(String q, Pageable pageable) {

        if (q == null || q.isBlank())
            return getAll(pageable);

        return repo.search(q, pageable)
                .map(this::map);
    }

    /* ===========================
       POSTS BY AUTHOR
    =========================== */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getByAuthor(Long authorId, Pageable pageable) {
        return repo.findByAuthorIdAndDeletedFalse(authorId, pageable)
                .map(this::map);
    }

    /* ===========================
       FUTURE FEATURE
       Count posts by author
    =========================== */
    public long countByAuthor(Long authorId) {
        return repo.countByAuthorIdAndDeletedFalse(authorId);
    }
}