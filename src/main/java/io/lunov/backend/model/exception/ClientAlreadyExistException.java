package io.lunov.backend.model.exception;

public class ClientAlreadyExistException extends RuntimeException {
    public ClientAlreadyExistException(String message) {
        super(message);
    }
}
