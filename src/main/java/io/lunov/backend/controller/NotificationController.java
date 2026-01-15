package io.lunov.backend.controller;

import io.lunov.backend.service.NotificationService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    @ResponseStatus(NO_CONTENT)
    public void sendNotification(@NotBlank @RequestParam(name = "session-id") String sessionId) {
        service.sendNotification(UUID.fromString(sessionId));
    }
}
