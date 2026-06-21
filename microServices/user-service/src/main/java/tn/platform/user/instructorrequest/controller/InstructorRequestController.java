package tn.platform.user.instructorrequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.platform.user.instructorrequest.dto.InstructorRequestResponse;
import tn.platform.user.instructorrequest.service.InstructorRequestService;

@RestController
@RequestMapping("/api/instructor-request")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InstructorRequestController {

    private final InstructorRequestService service;

    @PostMapping
    public ResponseEntity<InstructorRequestResponse> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String motivation,
            Authentication auth) {

        String email = auth.getName();

        InstructorRequestResponse response =
                service.createRequest(email, file, motivation);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @PutMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approve(@PathVariable Long id) {

        service.approveRequest(id);

        return ResponseEntity.ok("Request approved");
    }


    @PutMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reject(@PathVariable Long id) {

        service.rejectRequest(id);

        return ResponseEntity.ok("Request rejected");
    }


}