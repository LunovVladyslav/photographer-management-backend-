package io.lunov.backend.service.impl;

import io.lunov.backend.model.dto.image.ImageUploadResponse;
import io.lunov.backend.model.dto.photo.PhotoUploadDTO;
import io.lunov.backend.service.ImageService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    public static final String NONE_URL = "none";
    public static final String ORIGINAL_JPG = "_original.jpg";
    public static final String PREVIEW_JPG = "_preview.jpg";
    public static final String POLICY = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"AWS": ["*"]},
                        "Action": ["s3:GetObject"],
                        "Resource": ["arn:aws:s3:::%s/*"]
                    }
                ]
            }
            """;
    @Value("${minio.url}")
    private String minioUrl;

    private final MinioClient minioClient;

    @Override
    public ImageUploadResponse uploadImageWithPreview(UUID sessionId, PhotoUploadDTO payload) {
        String originalName = payload.getFileName() + ORIGINAL_JPG;
        String previewName = payload.getFileName() + PREVIEW_JPG;

        try {
            if (!isBucketExists(sessionId.toString())) {
                return ImageUploadResponse.builder()
                        .isSuccess(false)
                        .build();
            }
            MultipartFile file = payload.getFile();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(sessionId.toString())
                            .object(originalName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType("image/jpeg")
                            .build()
            );

            var previewBytes = createPreviewBytes(file.getInputStream());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(sessionId.toString())
                            .object(previewName)
                            .stream(new ByteArrayInputStream(previewBytes),
                                    previewBytes.length, -1)
                            .contentType("image/jpeg")
                            .build()
            );

            return ImageUploadResponse.builder()
                    .originalFileName(originalName)
                    .previewFileName(previewName)
                    .originalUrl(getPublicUrl(sessionId.toString(), originalName))
                    .previewUrl(getPublicUrl(sessionId.toString(), previewName))
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
          log.info("Something went wrong when uploading image with preview. {}", e.getMessage());
          return ImageUploadResponse.builder()
                  .originalFileName(originalName)
                  .previewFileName(previewName)
                  .originalUrl(NONE_URL)
                  .previewUrl(NONE_URL)
                  .isSuccess(false)
                  .build();
        }

    }

    @Override
    public String getPublicUrl(String bucketName, String objectName) {
        return String.format("%s/%s/%s", minioUrl, bucketName, objectName);
    }

    @Override
    public String getPresignedUrl(String bucketName, String objectName, int expiryDays) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiryDays, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Something went wrong when getting presigned URL. {}", e.getMessage());
            return NONE_URL;
        }
    }

    @Override
    public void createPreview(InputStream inputStream, ByteArrayOutputStream outputStream) {
        try {
            Thumbnails.of(inputStream)
                    .size(1920, 1080)
                    .outputQuality(0.85)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
        } catch (IOException e) {
            log.error("Something went wrong when uploading image with preview.", e);
        }
    }

    @Override
    public ByteArrayResource createZipArchive(String bucketName) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try(ZipOutputStream zos = new ZipOutputStream(baos)) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();

                if (objectName.contains(ORIGINAL_JPG)) {
                    try(GetObjectResponse response = minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .build()
                    )) {
                        ZipEntry zipEntry  = new ZipEntry(objectName);
                        zos.putNextEntry(zipEntry );
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = response.read(buffer)) != -1) {
                            zos.write(buffer, 0, length);
                        }

                        zos.closeEntry();
                        log.info("Added to archive: {}", objectName);
                    }
                }
            }
            zos.finish();
        }
        return new ByteArrayResource(baos.toByteArray());
    }

    @Override
    public byte[] createPreviewBytes(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        createPreview(input, output);
        return output.toByteArray();
    }

    @Override
    public boolean isBucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Something went wrong when check bucket exists. {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteImage(String bucketName, String fileName) {
        try {
            if (!isBucketExists(bucketName)) {
                log.warn("Bucket {} does not exist", bucketName);
                return;
            }

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();

                boolean contains = item.objectName().contains(fileName);

                log.info("Checking if image {} ({}) has been deleted: {}", fileName, item.objectName(), contains);
                if (contains) {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(item.objectName())
                                    .build()
                    );
                    log.info("Deleted object: {}/{}", bucketName, item.objectName());
                }
            }

        } catch (Exception e) {
            log.error("Failed to delete bucket {}: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete bucket: " + bucketName, e);
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        try {
            // Перевірити чи існує bucket
            if (!isBucketExists(bucketName)) {
                log.warn("Bucket {} does not exist", bucketName);
                return;
            }

            // 1. Видалити всі об'єкти в bucket
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
                log.info("Deleted object: {}/{}", bucketName, item.objectName());
            }

            // 2. Видалити сам bucket
            minioClient.removeBucket(
                    RemoveBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            log.info("Deleted bucket: {}", bucketName);

        } catch (Exception e) {
            log.error("Failed to delete bucket {}: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete bucket: " + bucketName, e);
        }
    }

}
