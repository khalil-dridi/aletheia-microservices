package com.esprit.microservices.postservice.repositories;

import com.esprit.microservices.postservice.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
