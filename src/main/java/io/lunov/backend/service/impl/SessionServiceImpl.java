package io.lunov.backend.service.impl;

import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.model.dto.session.*;
import io.lunov.backend.model.entity.*;
import io.lunov.backend.model.exception.SessionNotFoundException;
import io.lunov.backend.repository.PhotoRepository;
import io.lunov.backend.repository.SessionRepository;
import io.lunov.backend.service.ClientService;
import io.lunov.backend.service.ImageService;
import io.lunov.backend.service.SessionService;
import io.lunov.backend.specification.SessionSpecification;
import io.lunov.backend.util.AccessCodeGenerator;
import io.lunov.backend.util.mapper.SessionMapper;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;

import static io.lunov.backend.service.impl.ImageServiceImpl.POLICY;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {


    private final SessionRepository sessionRepository;
    private final PhotoRepository photoRepository;
    private final SessionMapper mapper;
    private final MinioClient minioClient;
    private final AccessCodeGenerator generator;
    private final ImageService imageService;
    private final ClientService clientService;

    private static final String NOT_FOUND = "Session not found. ID: %s";

    @Override
    public SessionInfoDTO save(SessionCreateDTO dto) {
        log.info("Saving new session: {}", dto);

        if (dto.getAccessType().equals(SessionAccessType.PUBLIC.toString())) {
            return createPortfolioSession(dto);
        }

        Session session = mapper.toEntity(dto);
        session.setAccessCode(generator.generateCode());
        session.setCreatedAt(Instant.now());

        var newSession = sessionRepository.save(session);
        createSessionBucket(newSession);

        return mapper.toDTO(newSession);
    }

    private void createSessionBucket(Session newSession) {
        String bucketName = newSession.getId().toString();
        try {
            log.info("MINIO::Making new bucket with id: {}", bucketName);
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket("%s".formatted(bucketName))
                    .build());
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(POLICY.formatted(bucketName))
                            .build()
            );
        } catch (Exception e) {
            log.error("Error making bucket for session: {}", bucketName, e);
        }
    }

    public SessionInfoDTO createPortfolioSession(SessionCreateDTO dto) {
        log.info("Creating new Portfolio Album: {}", dto);

        Client administrator;

        if (dto.getClientId() == null) {
            try {
                administrator = clientService.getByName("ADMINISTRATOR");
                log.info("Using existing administrator with id: {}", administrator.getId());
            } catch (Exception e) {
                log.info("Creating new admin user");
                var savedClientDTO = clientService.save(
                        ClientDTO.builder()
                                .name("ADMINISTRATOR")
                                .email("lunova.lisa@gmail.com")
                                .phoneNumber("602787394")
                                .build()
                );
                // Re-fetch to get managed entity
                administrator = clientService.getById(savedClientDTO.getId());
                log.info("Administrator created successfully with id: {}", administrator.getId());
            }
        } else {
            administrator = clientService.getById(dto.getClientId());
        }

        var savedSession = sessionRepository.save(Session.builder()
                .client(administrator)
                .createdAt(Instant.now())
                .accessCode(generator.generateCode())
                .photos(new ArrayList<>())
                .name(dto.getName())
                .contentType(SessionContentType.valueOf(dto.getContentType()))
                .accessType(SessionAccessType.valueOf(dto.getAccessType()))
                .build());

        createSessionBucket(savedSession);
        log.info("Portfolio Album created successfully with id: {}", savedSession.getId());
        return mapper.toDTO(savedSession);
    }

    @Override
    public SessionInfoDTO findById(UUID id) {
        log.info("Finding session with id: {}", id);
        var session = sessionRepository
                .findById(id)
                .orElseThrow(() -> new SessionNotFoundException(NOT_FOUND.formatted(id.toString())));

        return mapper.toDTO(session);
    }

    @Override
    public Session getById(UUID id) {
        return sessionRepository
                .findById(id)
                .orElseThrow(() -> new SessionNotFoundException(NOT_FOUND.formatted(id.toString())));
    }

    @Override
    public SessionInfoDTO findByAccessCodeAndClientEmail(SessionDownloadDTO payload) {
        ClientInfoDTO clientInfoDTO = clientService.findByEmail(payload.getClientEmail());
        Objects.requireNonNull(clientInfoDTO);

        var result = sessionRepository
                .findByAccessCodeAndClientEmail(payload.getAccessCode(), clientInfoDTO.getEmail())
                .orElseThrow(() -> new SessionNotFoundException(NOT_FOUND.formatted(payload.getAccessCode())));

        return mapper.toDTO(result);
    }

    @Override
    public List<SessionInfoDTO> findAll() {
        log.info("Finding all sessions");
        var result = sessionRepository.findAll();
        if (result.isEmpty()) {
            return List.of();
        }
        return result.stream().map(mapper::toDTO).toList();

    }

    @Override
    public List<SessionInfoDTO> findAllByClientId(UUID clientId) {
        log.info("Finding sessions by client ID: {}", clientId);
        var result = sessionRepository.findAllByClientId(clientId);
        if (result.isEmpty()) {
            return List.of();
        }
        return result.stream().map(mapper::toDTO).toList();
    }

    @Override
    public Optional<Session> findAllByContentTypeAndAccessType(String contentType, String accessType) {
        log.info("Finding sessions by content type and access type: {}, {}", contentType,  accessType);
        return sessionRepository.findByContentTypeAndAccessType(SessionContentType.valueOf(contentType), SessionAccessType.valueOf(accessType));
    }


    @Override
    public List<SessionInfoDTO> findAllByAccessType(String accessType) {
        log.info("Finding sessions by access-type: {}", accessType);
        var result = sessionRepository.findAllByAccessType(SessionAccessType.valueOf(accessType.toUpperCase()));
        if (result.isEmpty()) {
            return List.of();
        }
        return result.stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<SessionInfoDTO> findByFilters(SessionSearchDTO sessionInfoSearchDTO){
        log.info("Finding sessions by filters: {}", sessionInfoSearchDTO);
        List<Session> sessions;

       sessions = sessionRepository.findAll(SessionSpecification.withFilter(sessionInfoSearchDTO));
       if (sessions.isEmpty()) {
           return List.of();
       }

       return sessions.stream().map(mapper::toDTO).toList();
    }

    @Override
    public SessionInfoDTO update(UUID id, SessionUpdateDTO dto) {
        log.info("Updating session with id: {}", id);
        var result = sessionRepository
                .findById(id)
                .orElseThrow(() -> new SessionNotFoundException(NOT_FOUND.formatted(id.toString())));

        Optional.ofNullable(dto.getAccessType()).ifPresent(accessType -> result.setAccessType(SessionAccessType.valueOf(accessType.toUpperCase())));
        Optional.ofNullable(dto.getContentType()).ifPresent(contentType -> result.setContentType(SessionContentType.valueOf(contentType.toUpperCase())));
        Optional.ofNullable(dto.getName()).ifPresent(result::setName);

        var updated = sessionRepository.save(result);

        return mapper.toDTO(updated);
    }

    @Override
    public UUID updateEntity(@NotNull Session session) {
        log.info("Updating session (entity) with id: {}", session.getId());
        return sessionRepository.save(session).getId();
    }


    @Override
    public void delete(UUID id) {
        log.info("Deleting session with id: {}", id);
        var session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException(NOT_FOUND.formatted(id.toString())));
        if (session.getAccessType().equals(SessionAccessType.PUBLIC)) {
            deletePortfolioAlbum(id, session);
            return;
        }

        cleanupOrphanedPhotos(session);
        session.removeAllPhotos();
        sessionRepository.delete(session);
    }

    private void deletePortfolioAlbum(UUID id, Session session) {
        session.removeAllPhotos();
        imageService.deleteBucket(id.toString());
        sessionRepository.delete(session);
    }

    private void cleanupOrphanedPhotos(Session session) {
        var photos = new ArrayList<Photo>(session.getPhotos());
        photos.forEach(photo -> {
            if (photos.size() == 0) {
                try {
                    imageService.deleteImage(session.getId().toString(), photo.getFilename());
                    photoRepository.delete(photo);
                    log.info("Deleted orphaned photo: {}", photo.getId());
                } catch (Exception e) {
                    log.error("Failed to delete photo {} from MinIO", photo.getId(), e);
                }
            }
        });
    }

    @Override
    public SessionZipDTO downloadSessionAsZip(UUID sessionId) {
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(NOT_FOUND.formatted(sessionId.toString())));

        var result = SessionZipDTO.builder()
                .fileName(session.getName())
                .build();

        try {
//            InputStream zipStream = imageService.createZipArchive(sessionId.toString());
//            result.setSessionZip(Optional.of(new InputStreamResource(zipStream)));
            var zipResource = imageService.createZipArchive(sessionId.toString());
            result.setSessionZip(Optional.of(zipResource));

        } catch (Exception e) {
            log.error("Failed to create archive for session {}: {}", sessionId, e.getMessage());
            result.setSessionZip(Optional.empty());
        }

        return result;
    }
}
