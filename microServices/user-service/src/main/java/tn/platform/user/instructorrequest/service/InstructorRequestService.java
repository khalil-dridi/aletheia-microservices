package tn.platform.user.instructorrequest.service;

import org.springframework.web.multipart.MultipartFile;
import tn.platform.user.instructorrequest.dto.InstructorRequestResponse;
import tn.platform.user.instructorrequest.entity.InstructorRequest;

import java.util.List;

public interface InstructorRequestService {
    InstructorRequestResponse createRequest(
            String email,
            MultipartFile file,
            String motivation
    );

    List<InstructorRequestResponse> getAllRequests();
    void approveRequest(Long requestId);
    void rejectRequest(Long requestId);
}