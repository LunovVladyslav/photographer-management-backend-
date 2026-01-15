package io.lunov.backend.model.dto.session;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionUpdateDTO {
    private String name;
    private String accessType;
    private String contentType;
    private Instant sessionDate;
}
