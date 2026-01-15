package io.lunov.backend.service;

import io.lunov.backend.model.dto.image.ImageUploadResponse;
import io.lunov.backend.model.dto.photo.PhotoUploadDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

public interface ImageService {
    ImageUploadResponse uploadImageWithPreview(UUID sessionId, PhotoUploadDTO payload);
    String getPublicUrl(String bucketName, String objectName);
    String getPresignedUrl(String bucketName, String objectName, int expiryMinutes);
    void createPreview(InputStream inputStream, ByteArrayOutputStream outputStream);
    ByteArrayResource createZipArchive(String bucketName) throws Exception;
    byte[] createPreviewBytes(InputStream input);
    boolean isBucketExists(String bucketName);
    void deleteImage(String bucketName, String fileName);
    void deleteBucket(String bucketName);
}
