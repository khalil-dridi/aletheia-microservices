package tn.platform.community.reaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.platform.community.reaction.dto.*;
import tn.platform.community.reaction.service.ReactionService;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService service;

    @PostMapping
    public ResponseEntity<ReactionResponse> react(@RequestBody CreateReactionRequest req){
        return ResponseEntity.ok(service.react(req));
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(
            @RequestParam Long userId,
            @RequestParam Long targetId,
            @RequestParam String targetType){

        service.removeReaction(userId, targetId, targetType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam Long targetId,
            @RequestParam String targetType,
            @RequestParam String type){

        return ResponseEntity.ok(service.countReactions(targetId, targetType, type));
    }
}
