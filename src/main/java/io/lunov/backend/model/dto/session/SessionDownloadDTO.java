package io.lunov.backend.model.dto.session;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class SessionDownloadDTO {
    @NotBlank
    @Email
    private String clientEmail;
    @NotBlank
    private String accessCode;
}
