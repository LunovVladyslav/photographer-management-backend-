package io.lunov.backend.model.dto.photo;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PhotoShortInfoDTO {
    private UUID id;
    private String originalUrl;
    private String previewUrl;
}
