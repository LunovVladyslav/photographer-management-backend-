package io.lunov.backend.model.dto.session;

import io.lunov.backend.model.dto.photo.PhotoInfoDTO;
import io.lunov.backend.model.dto.photo.PhotoShortInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfoDTO {
    private UUID id;
    private String name;
    private List<PhotoShortInfoDTO> photos;
    private UUID clientId;
    private String accessType;
    private String contentType;
    private Instant sessionDate;
    private Instant createdAt;
}

