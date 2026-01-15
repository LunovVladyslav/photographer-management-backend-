package io.lunov.backend.model.dto.image;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUploadResponse {
    String originalFileName;
    String previewFileName;
    String originalUrl;
    String previewUrl;
    boolean isSuccess;
}
