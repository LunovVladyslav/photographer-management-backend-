package io.lunov.backend.model.dto.photo;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class PhotoInfoDTO {
    private UUID id;
    private UUID primarySessionId;  // Original session
    private List<UUID> sessionIds;
    private String originalUrl;
    private String previewUrl;
    private String filename;
    private Instant createdAt;
}
