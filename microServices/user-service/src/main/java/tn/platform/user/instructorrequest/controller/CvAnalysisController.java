package tn.platform.user.instructorrequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tn.platform.user.instructorrequest.service.CvPdfTextExtractor;
import tn.platform.user.instructorrequest.service.OllamaService;

import java.io.IOException;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CvAnalysisController {

    private static final long MAX_BYTES = 5 * 1024 * 1024;

    private final CvPdfTextExtractor cvPdfTextExtractor;
    private final OllamaService ollamaService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String analyze(@RequestParam("file") MultipartFile file) throws IOException {
        validatePdf(file);

        String extracted = cvPdfTextExtractor.extractText(file);
        if (extracted.isBlank()) {
            throw new IllegalArgumentException("No text could be extracted from the PDF");
        }

        return ollamaService.analyzeText(extracted);
    }

    private static void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !MediaType.APPLICATION_PDF_VALUE.equals(contentType)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Max file size is 5MB");
        }
    }
}
