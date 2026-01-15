package io.lunov.backend.service;

import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.model.entity.Client;
import io.lunov.backend.model.exception.ClientAlreadyExistException;
import io.lunov.backend.model.exception.ClientNotFoundException;
import io.lunov.backend.repository.ClientRepository;
import io.lunov.backend.service.impl.ClientServiceImpl;
import io.lunov.backend.util.mapper.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl  clientService;

    private ClientDTO clientDTO;
    private Client client;
    private ClientInfoDTO clientInfoDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        clientDTO = ClientDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("+380123456789")
                .build();

        client = Client.builder()
                .id(testId)
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("+380123456789")
                .sessions(List.of())
                .createdAt(Instant.now())
                .build();

        clientInfoDTO = ClientInfoDTO.builder()
                .id(testId)
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("+380123456789")
                .sessions(List.of())
                .createdAt(Instant.now())
                .build();

    }

    @Test
    @DisplayName("Should save new client")
    void shouldSaveNewClient() {
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientMapper.toEntity(clientDTO)).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toInfoDTO(client)).thenReturn(clientInfoDTO);

        ClientInfoDTO result = clientService.save(clientDTO);
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(client.getEmail());
        assertThat(result.getName()).isEqualTo(client.getName());

        verify(clientRepository, times(1)).existsByEmail(anyString());
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(clientMapper, times(1)).toInfoDTO(client);
        verify(clientMapper, times(1)).toEntity(clientDTO);
    }

    @Test
    @DisplayName("Should throw exception when client already exists")
    void shouldThrowExceptionWhenClientAlreadyExists() {
        when(clientRepository.existsByEmail(anyString())).thenReturn(true);
        assertThatThrownBy(() -> clientService.save(clientDTO))
                .isInstanceOf(ClientAlreadyExistException.class)
                .hasMessageContaining("john.doe@example.com");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should find client by ID successfully")
    void shouldFindClientById() {
        when(clientRepository.findById(testId)).thenReturn(Optional.of(client));
        when(clientMapper.toInfoDTO(client)).thenReturn(clientInfoDTO);

        ClientInfoDTO result = clientService.findById(testId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);

        verify(clientRepository, times(1)).findById(testId);
        verify(clientMapper, times(1)).toInfoDTO(client);
    }

    @Test
    @DisplayName("Should throw exception when client not found by ID")
    void shouldThrowExceptionWhenClientNotFound() {
        when(clientRepository.findById(testId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> clientService.findById(testId))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining(testId.toString());
        verify(clientRepository, times(1)).findById(testId);
        verify(clientMapper, never()).toInfoDTO(any());
    }

    @Test
    @DisplayName("Should find client by name successfully")
    void shouldFindClientByName() {
        when(clientRepository.findByName(client.getName())).thenReturn(Optional.of(client));
        when(clientMapper.toInfoDTO(client)).thenReturn(clientInfoDTO);

        ClientInfoDTO result = clientService.findByName(client.getName());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(client.getName());

        verify(clientRepository, times(1)).findByName(client.getName());
    }

    @Test
    @DisplayName("Should return all clients")
    void shouldFindAllClients() {
        List<Client> clients = List.of(client);

        when(clientRepository.findAll()).thenReturn(clients);
        when(clientMapper.toInfoDTO(client)).thenReturn(clientInfoDTO);

        List<ClientInfoDTO> result = clientService.findAll();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);

        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no clients")
    void shouldReturnEmptyListWhenNoClients() {
        when(clientRepository.findAll()).thenReturn(List.of());

        List<ClientInfoDTO> result = clientService.findAll();
        assertThat(result.isEmpty()).isTrue();

        verify(clientRepository, times(1)).findAll();
        verify(clientMapper, never()).toInfoDTO(any());
    }

    @Test
    @DisplayName("Should update client successfully")
    void shouldUpdateClient() {
        when(clientRepository.findById(testId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toInfoDTO(client)).thenReturn(clientInfoDTO);

        ClientInfoDTO result = clientService.update(testId, clientDTO);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);

        verify(clientRepository, times(1)).findById(testId);
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(clientMapper, times(1)).toInfoDTO(client);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent client")
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        when(clientRepository.findById(testId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> clientService.update(testId, clientDTO))
                .isInstanceOf(ClientNotFoundException.class);
        verify(clientRepository, times(1)).findById(testId);
        verify(clientMapper, never()).toInfoDTO(any());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should delete client successfully")
    void shouldDeleteClient() {
        when(clientRepository.existsById(testId)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(testId);
        clientService.delete(testId);
        verify(clientRepository, times(1)).existsById(testId);
        verify(clientRepository, times(1)).deleteById(testId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent client")
    void shouldThrowExceptionWhenDeletingNonExistentClient() {
        when(clientRepository.existsById(testId)).thenReturn(false);
        assertThatThrownBy(() -> clientService.delete(testId))
                .isInstanceOf(ClientNotFoundException.class);
        verify(clientRepository, times(1)).existsById(testId);
        verify(clientRepository, never()).deleteById(testId);
    }

    @Test
    @DisplayName("Should update only non-null fields")
    void shouldUpdateOnlyNonNullFields() {
        ClientDTO partialUpdate = ClientDTO.builder()
                .name("Updated Name")
                .email(null)  // email не оновлюємо
                .phoneNumber(null)  // phone не оновлюємо
                .build();

        when(clientRepository.findById(testId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toInfoDTO(client)).thenReturn(clientInfoDTO);

        clientService.update(testId, partialUpdate);
        verify(clientRepository, times(1)).save(argThat(c ->
                c.getName().equals("Updated Name") &&
                        c.getEmail().equals("john.doe@example.com")  // старий email залишився
        ));
    }
}