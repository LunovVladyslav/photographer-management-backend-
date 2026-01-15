package io.lunov.backend.service.impl;

import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.model.entity.Client;
import io.lunov.backend.model.exception.ClientAlreadyExistException;
import io.lunov.backend.model.exception.ClientNotFoundException;
import io.lunov.backend.repository.ClientRepository;
import io.lunov.backend.service.ClientService;
import io.lunov.backend.util.mapper.ClientMapper;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    private static final String CLIENT_EXIST_MESSAGE = "Client with email: %s - already exist";
    private static final String CLIENT_NOT_EXIST_MESSAGE = "Client with id/name: %s - doesn't exist";

    @Override
    @Transactional
    public ClientInfoDTO save(ClientDTO client) {
        log.info("Saving new client with email: {}", client.getEmail());
        if (repository.existsByEmail(client.getEmail())) {
            throw new ClientAlreadyExistException(CLIENT_EXIST_MESSAGE.formatted(client.getEmail()));
        }

        Client newClient = mapper.toEntity(client);

        var savedClient = repository.save(newClient);
        log.info("Client with email: {} has been saved", client.getEmail());

        return mapper.toInfoDTO(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientInfoDTO findById(UUID id) {
        log.info("Finding client with id: {}", id);
        var client = repository
                .findById(id)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(id)));
        return mapper.toInfoDTO(client);
    }

    @Override
    public Client getById(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(id)));
    }

    @Override
    public Client getByName(String name) {
        return repository
                .findByName(name)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(name)));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientInfoDTO findByName(String name) {
        log.info("Finding client with name: {}", name);
        var client = repository
                .findByName(name)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(name)));
        return mapper.toInfoDTO(client);
    }

    @Override
    public ClientInfoDTO findByEmail(String email) {
        log.info("Finding client with email: {}", email);
        var client = repository
                .findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(email)));
        return mapper.toInfoDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientInfoDTO> findAll() {
        log.info("Finding all clients");
        var clients = repository.findAll();
        if (clients.isEmpty()) {
            log.info("No client found");
            return List.of();
        }

        return clients.stream().map(mapper::toInfoDTO).toList();
    }

    @Override
    @Transactional
    public ClientInfoDTO update(UUID id, ClientDTO client) {
        log.info("Updating client with id: {}", id);
        var existingClient = repository
                .findById(id)
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(id)));

        Optional.ofNullable(client.getName()).ifPresent(existingClient::setName);
        Optional.ofNullable(client.getEmail()).ifPresent(existingClient::setEmail);
        Optional.ofNullable(client.getPhoneNumber()).ifPresent(existingClient::setPhoneNumber);

        var updatedClient = repository.save(existingClient);
        log.info("Client with id: {} has been updated", id);
        return mapper.toInfoDTO(updatedClient);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting client with id: {}", id);
        if (!repository.existsById(id)) {
           throw new ClientNotFoundException(CLIENT_NOT_EXIST_MESSAGE.formatted(id));
        }
        repository.deleteById(id);
        log.info("Client with id: {} has been deleted", id);
    }
}
