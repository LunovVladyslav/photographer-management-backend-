package io.lunov.backend.model.dto.error;

public class PhotoNotFoundException extends RuntimeException {
    public PhotoNotFoundException(String message) {
        super("Photo ot found: %s".formatted(message));
    }
}
