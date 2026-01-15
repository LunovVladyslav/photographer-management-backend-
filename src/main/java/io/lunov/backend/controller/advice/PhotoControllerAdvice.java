package io.lunov.backend.controller.advice;

import io.lunov.backend.model.dto.error.ErrorResponseDTO;
import io.lunov.backend.model.dto.error.PhotoNotFoundException;
import io.lunov.backend.model.exception.ClientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class PhotoControllerAdvice {

    @ExceptionHandler(PhotoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleException(ClientNotFoundException e) {
        return ErrorResponseDTO.builder()
                .message(e.getMessage())
                .error("Photo not found")
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .build();
    }
}
