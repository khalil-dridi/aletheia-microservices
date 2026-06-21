package tn.platform.user.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto") // 🔥 IMPORTANT
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            if (file.getContentType() == null || !file.getContentType().equals("application/pdf")) {
                throw new IllegalArgumentException("Only PDF files are allowed");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Max file size is 5MB");
            }

            String originalName = file.getOriginalFilename();

            String publicId = (originalName != null ? originalName : "file")
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    .toLowerCase() + "_" + System.currentTimeMillis();

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "public_id", publicId,

                            // ✅ LA SEULE LIGNE IMPORTANTE
                            "access_control", "[{\"access_type\":\"anonymous\"}]"
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Error uploading file: " + e.getMessage());
        }
    }
}