package io.lunov.backend.model.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private String error;
    private int status;
    private Instant timestamp;
}
