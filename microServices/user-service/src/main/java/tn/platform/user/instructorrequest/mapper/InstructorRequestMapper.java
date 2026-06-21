package tn.platform.user.instructorrequest.mapper;


import org.springframework.stereotype.Component;
import tn.platform.user.instructorrequest.dto.InstructorRequestResponse;
import tn.platform.user.instructorrequest.entity.InstructorRequest;

@Component
public class InstructorRequestMapper {

    public InstructorRequestResponse toResponse(InstructorRequest req) {
        return InstructorRequestResponse.builder()
                .id(req.getId())
                .userId(req.getUser().getId())
                .userName(req.getUser().getNom() + " " + req.getUser().getPrenom())
                .cvUrl(req.getCvUrl())
                .score(req.getScore())
                .aiFeedback(req.getAiFeedback())
                .status(req.getStatus())
                .createdAt(req.getCreatedAt())
                .build();
    }
}
