package io.lunov.backend.model.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateDTO {
    private String name;
    private UUID clientId;
    private String accessType;
    private String contentType;
    private String sessionDate;
}
