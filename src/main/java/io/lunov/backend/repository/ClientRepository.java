package io.lunov.backend.repository;

import io.lunov.backend.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    Optional<Client> findByName(String name);
}
