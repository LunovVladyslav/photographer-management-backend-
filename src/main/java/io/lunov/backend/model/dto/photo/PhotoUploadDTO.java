package io.lunov.backend.model.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class PhotoUploadDTO {
    private String fileName;
    private MultipartFile file;
}
