package io.lunov.backend.controller;

import io.lunov.backend.model.dto.session.*;
import io.lunov.backend.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Validated
public class SessionController {
    private final SessionService service;

    @GetMapping
    @ResponseStatus(OK)
    public List<SessionInfoDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public SessionInfoDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping("/search")
    @ResponseStatus(OK)
    public List<SessionInfoDTO> search(
           @RequestBody @Valid SessionSearchDTO sessionInfoSearchDTO
    ) {
        return service.findByFilters(sessionInfoSearchDTO);
    }

    @GetMapping("/search/client/{id}")
    @ResponseStatus(OK)
    public List<SessionInfoDTO> findAllByClientId(@PathVariable UUID id) {
        return service.findAllByClientId(id);
    }

    @PostMapping("/new")
    @ResponseStatus(CREATED)
    public SessionInfoDTO create(@RequestBody @Valid SessionCreateDTO payload) {
        return service.save(payload);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(OK)
    public SessionInfoDTO update(@PathVariable UUID id, @RequestBody @Valid SessionUpdateDTO payload) {
        return service.update(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/{id}/download")
    @ResponseStatus(OK)
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        try {
            var result = service.downloadSessionAsZip(id);
            var resource = result.getSessionZip().orElseThrow();

            String filename = result.getFileName() + ".zip";
            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"download.zip\"; filename*=UTF-8''" + encoded)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/find")
    @ResponseStatus(OK)
    public SessionInfoDTO findClientSession(@Valid @RequestBody SessionDownloadDTO payload) {
        return service.findByAccessCodeAndClientEmail(payload);
    }

}
