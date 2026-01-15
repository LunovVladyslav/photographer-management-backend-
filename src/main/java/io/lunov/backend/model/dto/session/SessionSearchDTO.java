package io.lunov.backend.model.dto.session;

import io.lunov.backend.model.entity.SessionAccessType;
import io.lunov.backend.model.entity.SessionContentType;
import io.lunov.backend.model.validation.SessionInfo;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@SessionInfo
@Valid
@ToString
public class SessionSearchDTO {
    private String clientName;
    private SessionAccessType accessType;
    private SessionContentType contentType;
    private Instant createdAfter;
    private Instant createdBefore;
}
