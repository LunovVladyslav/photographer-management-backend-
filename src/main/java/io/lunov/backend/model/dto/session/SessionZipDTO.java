package io.lunov.backend.model.dto.session;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;

import java.util.Optional;

@Data
@Builder
public class SessionZipDTO {
    Optional<ByteArrayResource> sessionZip;
    String fileName;
}
