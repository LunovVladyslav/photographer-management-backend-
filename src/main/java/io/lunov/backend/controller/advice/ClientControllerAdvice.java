package io.lunov.backend.controller.advice;

import io.lunov.backend.model.dto.error.ErrorResponseDTO;
import io.lunov.backend.model.exception.ClientAlreadyExistException;
import io.lunov.backend.model.exception.ClientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ClientControllerAdvice {

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleException(ClientNotFoundException e) {
        return ErrorResponseDTO.builder()
                .message(e.getMessage())
                .error("Client not found")
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(ClientAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleException(ClientAlreadyExistException e) {
        return ErrorResponseDTO.builder()
                .message(e.getMessage())
                .error("Client already exist")
                .status(HttpStatus.CONFLICT.value())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleException(IllegalArgumentException e) {
        return ErrorResponseDTO.builder()
                .message(e.getMessage())
                .error("Illegal argument")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ErrorResponseDTO.builder()
                .message("Validation failed")
                .error(errors.toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .build();
    }
}
