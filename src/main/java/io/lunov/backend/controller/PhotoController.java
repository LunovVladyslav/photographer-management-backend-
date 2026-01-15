package io.lunov.backend.controller;

import io.lunov.backend.model.dto.photo.PhotoInfoDTO;
import io.lunov.backend.service.PhotoService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService service;

    @PostMapping(value = "/{sessionId}/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    public List<PhotoInfoDTO> addPhotos(
            @PathVariable UUID sessionId,
            @RequestParam("files") List<MultipartFile> files) {

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No files provided");
        }

        if (files.size() > 50) { // ліміт
            throw new IllegalArgumentException("Too many files. Maximum 50 files allowed");
        }

        return service.saveMultiple(sessionId, files);
    }

    @PostMapping("/add/portfolio")
    @ResponseStatus(CREATED)
    public UUID addPhotoToPortfolio(
            @RequestParam(name = "content-type") String contentType,
            @RequestParam(name = "photo-id") String photoId
    ) {
        return service.addPhotoToPortfolio(contentType, UUID.fromString(photoId));
    }

    @GetMapping("/{sessionId}")
    @ResponseStatus(OK)
    public List<PhotoInfoDTO> findAllBySessionId(@PathVariable UUID sessionId) {
        return service.findAllBySessionId(sessionId);
    }

    @DeleteMapping("/{sessionId}/photos/{photoId}")
    @ResponseStatus(NO_CONTENT)
    public void deletePhoto(@PathVariable UUID sessionId, @PathVariable UUID photoId, @RequestParam @NotBlank String fileName) {
        service.delete(sessionId, photoId, fileName);
    }

}
