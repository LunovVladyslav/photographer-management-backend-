package io.lunov.backend.model.dto.client;

import io.lunov.backend.model.validation.AccessCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class ClientInfoDTO {
    private UUID id;
    @AccessCode
    @Valid
    private String name;
    @NotNull
    @Email
    private String email;
    private String phoneNumber;
    private List<UUID> sessions;
    private Instant createdAt;
}
