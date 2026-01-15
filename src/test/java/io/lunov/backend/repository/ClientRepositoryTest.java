package io.lunov.backend.repository;

import io.lunov.backend.model.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ClientRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        testClient = Client.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("123456789")
                .sessions(List.of())
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should save client and generate id")
    void shouldSaveClientAndGenerateId() {
        var savedClient = repository.save(testClient);

        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getName()).isEqualTo(testClient.getName());
        assertThat(savedClient.getEmail()).isEqualTo(testClient.getEmail());
        assertThat(savedClient.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find client by email")
    void shouldFindClientByEmail() {
        repository.save(testClient);

        Client findedClient = repository.findByEmail(testClient.getEmail()).orElse(null);

        assertThat(findedClient).isNotNull();
        assertThat(findedClient.getName()).isEqualTo(testClient.getName());
        assertThat(findedClient.getEmail()).isEqualTo(testClient.getEmail());
        assertThat(findedClient.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty when client not found by email")
    void shouldNotFoundClientData() {
        Optional<Client> found = repository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find client by name")
    void shouldFindClientByName() {
        repository.save(testClient);

        Client foundClient = repository.findByName(testClient.getName()).orElse(null);
        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getEmail()).isEqualTo(testClient.getEmail());
    }

    @Test
    @DisplayName("Should check if client exists by email")
    void shouldCheckIfClientExistsByEmail() {
        repository.save(testClient);

        boolean exists = repository.existsByEmail(testClient.getEmail());
        boolean notExists = repository.existsByEmail("other.mail@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find all clients")
    void shouldFindAllClients() {
        repository.save(testClient);

        Client otherClient = Client.builder()
                .name("Jack Black")
                .email("jack.black@example.com")
                .phoneNumber("111222333")
                .sessions(List.of())
                .createdAt(Instant.now())
                .build();

        repository.save(otherClient);

        var allClients = repository.findAll();

        assertThat(allClients).hasSize(2);
        assertThat(allClients).extracting(Client::getName)
                .containsExactlyInAnyOrder("John Doe", "Jack Black");
    }

    @Test
    @DisplayName("Should delete client by id")
    void shouldDeleteClientById() {
        // Given
        Client saved = repository.save(testClient);
        UUID clientId = saved.getId();

        // When
        repository.deleteById(clientId);

        // Then
        assertThat(repository.findById(clientId)).isEmpty();
    }

    @Test
    @DisplayName("Should update client")
    void shouldUpdateClient() {
        // Given
        Client saved = repository.save(testClient);

        // When
        saved.setEmail("newemail@example.com");
        Client updated = repository.save(saved);

        // Then
        assertThat(updated.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }
}
