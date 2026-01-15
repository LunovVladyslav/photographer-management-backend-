package io.lunov.backend.service;

import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.model.entity.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientInfoDTO save(ClientDTO client);
    ClientInfoDTO findById(UUID id);
    Client getById(UUID id);
    Client getByName(String email);
    ClientInfoDTO findByName(String name);
    ClientInfoDTO findByEmail(String email);
    List<ClientInfoDTO> findAll();
    ClientInfoDTO update(UUID id, ClientDTO client);
    void delete(UUID id);
}
