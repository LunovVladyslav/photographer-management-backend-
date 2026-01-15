package io.lunov.backend.service;

import io.lunov.backend.model.dto.photo.PhotoInfoDTO;
import io.lunov.backend.model.dto.photo.PhotoUploadDTO;
import io.lunov.backend.model.dto.session.SessionInfoDTO;
import io.lunov.backend.model.entity.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoService {
    PhotoInfoDTO save(UUID sessionId, PhotoUploadDTO dto);
    List<PhotoInfoDTO> saveMultiple(UUID sessionId, List<MultipartFile> files);
    List<PhotoInfoDTO> findAllBySessionId(UUID sessionId);
    Photo getById(UUID id);
    PhotoInfoDTO findById(UUID id);
    void delete(UUID sessionId,  UUID photoId, String fileName);
    UUID addPhotoToPortfolio(String contentType, UUID photoId);

}
