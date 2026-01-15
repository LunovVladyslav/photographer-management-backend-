package io.lunov.backend.model.validation;

import org.springframework.http.HttpStatus;

public class PhotoUploadException extends Throwable {
    public PhotoUploadException(String string) {
        super(string);
    }
}
