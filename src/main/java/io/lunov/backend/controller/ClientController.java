package io.lunov.backend.controller;

import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientInfoDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public ClientInfoDTO findByName(@Valid @RequestParam String name) {
        return service.findByName(name);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientInfoDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientInfoDTO create(@Valid @RequestBody ClientDTO client) {
        return service.save(client);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientInfoDTO update(@PathVariable UUID id, @Valid @RequestBody ClientDTO client) {
        return service.update(id, client);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
