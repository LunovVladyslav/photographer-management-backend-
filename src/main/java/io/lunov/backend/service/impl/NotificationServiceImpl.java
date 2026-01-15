package io.lunov.backend.service.impl;

import io.lunov.backend.model.exception.NotificationSendException;
import io.lunov.backend.model.exception.SessionNotFoundException;
import io.lunov.backend.repository.ClientRepository;
import io.lunov.backend.repository.SessionRepository;
import io.lunov.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final SessionRepository sessionRepository;

    @Override
    public void sendNotification(UUID sessionId) {
        SimpleMailMessage message = new SimpleMailMessage();

        var session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        var client = session.getClient();

        message.setTo(client.getEmail());
        message.setSubject("Ваша фотосесія готова: " + session.getName());
        message.setText(
                "Вітаємо!\n\n" +
                        "Ваша фотосесія '" + session.getName() + "' готова.\n\n" +
                        "Ваш код доступу: " + session.getAccessCode() + "\n\n" +
                        "Перейдіть на сайт та введіть цей код для перегляду фотографій.\n\n"
        );

        try {
            mailSender.send(message);
            log.info("Notification sent successfully to %s".formatted(client.getEmail()));
        } catch (MailException e) {
            log.error("Failed to send notification to %s : %s".formatted(client.getEmail(), e.getMessage()));
            throw new NotificationSendException("Failed to send notification to client. %s".formatted(e.getMessage()));
        }
    }
}
