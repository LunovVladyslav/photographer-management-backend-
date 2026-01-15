package io.lunov.backend.service;

import io.lunov.backend.model.dto.session.SessionInfoDTO;

import java.util.UUID;

public interface NotificationService {
    void sendNotification(UUID sessionId);
}
