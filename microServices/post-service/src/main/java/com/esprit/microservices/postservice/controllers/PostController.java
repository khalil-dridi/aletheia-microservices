package com.esprit.microservices.postservice.controllers;

import com.esprit.microservices.postservice.entities.Post;
import com.esprit.microservices.postservice.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    // field to be initialized by Lombok-generated constructor
    private final PostRepository postRepository;

    @GetMapping
    public List<Post> getAll() {
        return postRepository.findAll();
    }
}