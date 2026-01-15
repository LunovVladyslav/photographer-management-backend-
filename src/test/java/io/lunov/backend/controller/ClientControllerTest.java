package io.lunov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lunov.backend.controller.advice.ClientControllerAdvice;
import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.model.exception.ClientAlreadyExistException;
import io.lunov.backend.model.exception.ClientNotFoundException;
import io.lunov.backend.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ClientService service;

    @InjectMocks
    private ClientController controller;

    private ClientDTO clientDTO;
    private ClientInfoDTO clientInfoDTO;
    private UUID testId;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ClientControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        testId = UUID.randomUUID();

        clientDTO = ClientDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("123456789")
                .build();

        clientInfoDTO = ClientInfoDTO.builder()
                .id(testId)
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("123456789")
                .sessions(List.of())
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/clients should return all clients")
    void shouldReturnAllClients() throws Exception {
        // Given
        List<ClientInfoDTO> clients = List.of(clientInfoDTO);
        when(service.findAll()).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",    hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")));

        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return client by ID")
    void shouldReturnClientById() throws Exception {
        // Given
        when(service.findById(any(UUID.class))).thenReturn(clientInfoDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/clients/{id}", testId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(service, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should create new client")
    void shouldCreateNewClient() throws Exception {
        // Given
        when(service.save(any(ClientDTO.class))).thenReturn(clientInfoDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(service, times(1)).save(any(ClientDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when client not found")
    void shouldReturn404WhenClientNotFound() throws Exception {
        // Given
        when(service.findById(any(UUID.class)))
                .thenThrow(new ClientNotFoundException("Client not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/clients/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 409 when client already exists")
    void shouldReturn409WhenClientAlreadyExists() throws Exception {
        // Given
        when(service.save(any(ClientDTO.class)))
                .thenThrow(new ClientAlreadyExistException("Client already exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should update client")
    void shouldUpdateClient() throws Exception {
        // Given

        when(service.update(testId, clientDTO)).thenReturn(clientInfoDTO);

        // When & Then
        mockMvc.perform(patch("/api/v1/clients/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isOk());

        verify(service, times(1)).update(testId, clientDTO);
    }

    @Test
    @DisplayName("Should delete client")
    void shouldDeleteClient() throws Exception {
        // Given
        doNothing().when(service).delete(any(UUID.class));

        // When & Then
        mockMvc.perform(delete("/api/v1/clients/{id}", testId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(any(UUID.class));
    }

}