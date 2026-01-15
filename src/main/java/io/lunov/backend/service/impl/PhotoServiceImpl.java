package io.lunov.backend.service.impl;

import io.lunov.backend.model.dto.error.PhotoNotFoundException;
import io.lunov.backend.model.dto.image.ImageUploadResponse;
import io.lunov.backend.model.dto.photo.PhotoInfoDTO;
import io.lunov.backend.model.dto.photo.PhotoUploadDTO;
import io.lunov.backend.model.dto.session.SessionCreateDTO;
import io.lunov.backend.model.dto.session.SessionInfoDTO;
import io.lunov.backend.model.entity.Photo;
import io.lunov.backend.model.entity.Session;
import io.lunov.backend.model.entity.SessionAccessType;
import io.lunov.backend.repository.PhotoRepository;
import io.lunov.backend.service.ImageService;
import io.lunov.backend.service.PhotoService;
import io.lunov.backend.service.SessionService;
import io.lunov.backend.util.mapper.PhotoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final ImageService imageService;
    private final SessionService sessionService;
    private final PhotoRepository repository;
    private final PhotoMapper mapper;


    @Override
    public PhotoInfoDTO save(UUID sessionId, PhotoUploadDTO dto) {
        Session session = sessionService.getById(sessionId);

        ImageUploadResponse response = imageService.uploadImageWithPreview(sessionId, dto);

        Photo photo = Photo.builder()
                .filename(dto.getFileName())
                .primarySession(session)  // Keep reference to original session
                .originalUrl(response.getOriginalUrl())
                .previewUrl(response.getPreviewUrl())
                .createdAt(Instant.now())
                .sessions(new ArrayList<>())
                .build();

        // Add to session
        session.addPhoto(photo);

        Photo savedPhoto = repository.save(photo);
        return mapper.toDto(savedPhoto);
    }

//    @Override
//    public List<PhotoInfoDTO> saveMultiple(UUID sessionId, List<MultipartFile> files) {
//        return files.parallelStream()
//                .map(file -> {
//                    String fileName = "photo_" + UUID.randomUUID().toString().substring(0, 8);
//
//                    PhotoUploadDTO payload = PhotoUploadDTO.builder()
//                            .file(file)
//                            .fileName(fileName)
//                            .build();
//
//                    try {
//                        return save(sessionId, payload);
//                    } catch (Exception e) {
//                        log.error("Failed to upload file {}: {}",
//                                file.getOriginalFilename(), e.getMessage());
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toList();
//    }

    @Override
    @Transactional
    public List<PhotoInfoDTO> saveMultiple(UUID sessionId, List<MultipartFile> files) {
        Session session = sessionService.getById(sessionId);
        session.getPhotos().size();

        return files.stream() // Use sequential stream instead of parallel
                .map(file -> {
                    String fileName = "photo_" + UUID.randomUUID().toString().substring(0, 8);

                    PhotoUploadDTO payload = PhotoUploadDTO.builder()
                            .file(file)
                            .fileName(fileName)
                            .build();

                    try {
                        return save(sessionId, payload);
                    } catch (Exception e) {
                        log.error("Failed to upload file {}: {}",
                                file.getOriginalFilename(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<PhotoInfoDTO> findAllBySessionId(UUID sessionId) {
        var response = repository.findBySessionId(sessionId);
        return response.stream().map(mapper::toDto).toList();
    }

    @Override
    public Photo getById(UUID id) {
        return repository.findById(id).orElseThrow(
                    () -> new PhotoNotFoundException("Photo with id: %s - not found!".formatted(id))
            );

    }

    @Override
    public PhotoInfoDTO findById(UUID id) {
           var photo = repository.findById(id).orElseThrow(() -> new PhotoNotFoundException("id - %s".formatted(id)));
           return mapper.toDto(photo);
    }


    @Override
    @Transactional
    public UUID addPhotoToPortfolio(String contentType, UUID photoId) {
        log.info("Adding Photo {} to Portfolio Album: {}", photoId, contentType);

        // Get the photo
        Photo photo = getById(photoId);

        // Find or create portfolio session
        var portfolioSessionOpt = sessionService.findAllByContentTypeAndAccessType(
                contentType,
                SessionAccessType.PUBLIC.toString()
        );

        Session portfolioSession;

        if (portfolioSessionOpt.isEmpty()) {
            // Create new portfolio session
            SessionInfoDTO newPortfolio = sessionService.createPortfolioSession(
                    SessionCreateDTO.builder()
                            .name(contentType)
                            .accessType(SessionAccessType.PUBLIC.toString())
                            .contentType(contentType)
                            .sessionDate(LocalDate.now().format(
                                    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                            ))
                            .build()
            );
            portfolioSession = sessionService.getById(newPortfolio.getId());
        } else {
            portfolioSession = portfolioSessionOpt.get();
        }

        // Check if photo is already in this portfolio
        if (!portfolioSession.getPhotos().contains(photo)) {
            portfolioSession.addPhoto(photo);
            sessionService.updateEntity(portfolioSession);
        } else {
            log.info("Photo {} already exists in portfolio {}", photoId, contentType);
        }

        return photo.getId();
    }

    @Override
    @Transactional
    public void delete(UUID sessionId, UUID photoId, String fileName) {
        if (!repository.existsById(photoId)) {
            log.info("Photo with id {} does not exist", photoId);
            return;
        }

        Photo photo = getById(photoId);
        Session session = sessionService.getById(sessionId);

        // Remove photo from this specific session
        session.removePhoto(photo);
        sessionService.updateEntity(session);

        // If photo is not in any other sessions and this was its primary session, delete it
        if (photo.getSessions().isEmpty() ||
                (photo.getPrimarySession() != null &&
                        photo.getPrimarySession().getId().equals(sessionId))) {

            // Delete from storage
            if (imageService.isBucketExists(sessionId.toString())) {
                imageService.deleteImage(sessionId.toString(), fileName);
            }

            // Delete from database
            repository.deleteById(photoId);
            log.info("Photo with id {} has been completely deleted", photoId);
        } else {
            log.info("Photo with id {} removed from session {} but kept in other sessions",
                    photoId, sessionId);
        }
    }
}
