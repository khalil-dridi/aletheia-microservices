package tn.platform.community.post.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import tn.platform.community.post.dto.CreatePostRequest;
import tn.platform.community.post.dto.PostResponse;

public interface PostService {

    /* ================= CREATE ================= */

    // Create simple post (JSON)
    PostResponse create(CreatePostRequest request);

    // Create post with optional images
    PostResponse createWithImages(Long authorId,
                                  String content,
                                  MultipartFile[] images);

    /* ================= GET ================= */

    Page<PostResponse> getAll(Pageable pageable);

    PostResponse getById(Long id);

    Page<PostResponse> search(String q, Pageable pageable);

    Page<PostResponse> getByAuthor(Long authorId, Pageable pageable);

    /* ================= UPDATE ================= */

    PostResponse updateWithImages(Long id,
                                  String content,
                                  MultipartFile[] images);

    /* ================= DELETE ================= */

    void delete(Long id);

    /* ================= FUTURE ================= */

    long countByAuthor(Long authorId);
}