package tn.platform.user.instructorrequest.dto;


import lombok.Builder;
import lombok.Data;
import tn.platform.user.instructorrequest.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder

public class InstructorRequestResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String cvUrl;
    private Double score;
    private String aiFeedback;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}