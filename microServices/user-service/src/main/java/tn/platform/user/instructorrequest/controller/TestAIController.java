package tn.platform.user.instructorrequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.platform.user.instructorrequest.service.OllamaService;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestAIController {

    private final OllamaService ollamaService;

    @GetMapping
    public String test() {
        return ollamaService.analyzeText(
                "Développeur Java avec 2 ans d'expérience en Spring Boot"
        );
    }

    @PostMapping
    public String analyze(@RequestBody String text) {
        return ollamaService.analyzeText(text);
    }
}