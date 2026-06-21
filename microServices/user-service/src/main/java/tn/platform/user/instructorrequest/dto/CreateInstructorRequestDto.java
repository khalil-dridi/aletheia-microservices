package tn.platform.user.instructorrequest.dto;

import lombok.Data;

@Data
public class CreateInstructorRequestDto {

    // pour l’instant on garde simple
    // plus tard on ajoutera MultipartFile (upload PDF)

    private String cvUrl;
    private String motivation;
}