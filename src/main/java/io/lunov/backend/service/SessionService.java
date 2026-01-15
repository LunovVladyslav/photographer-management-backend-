package io.lunov.backend.service;

import io.lunov.backend.model.dto.session.*;
import io.lunov.backend.model.entity.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionService {
    SessionInfoDTO save(SessionCreateDTO dto);
    SessionInfoDTO createPortfolioSession(SessionCreateDTO dto);
    SessionInfoDTO findById(UUID id);
    Session getById(UUID id);
    SessionInfoDTO findByAccessCodeAndClientEmail(SessionDownloadDTO payload);
    List<SessionInfoDTO> findAll();
    List<SessionInfoDTO> findAllByClientId(UUID clientId);
    Optional<Session> findAllByContentTypeAndAccessType(String contentType, String accessType);
    List<SessionInfoDTO> findAllByAccessType(String accessType);
    List<SessionInfoDTO> findByFilters(SessionSearchDTO sessionInfoSearchDTO);
    SessionInfoDTO update(UUID id, SessionUpdateDTO dto);
    UUID updateEntity(Session session);
    void delete(UUID id);
    SessionZipDTO downloadSessionAsZip(UUID sessionId);

}
