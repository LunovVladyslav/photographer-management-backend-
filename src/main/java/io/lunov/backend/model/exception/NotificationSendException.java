package io.lunov.backend.model.exception;

public class NotificationSendException extends RuntimeException {
    public NotificationSendException(String message) {
        super(message);
    }
}
