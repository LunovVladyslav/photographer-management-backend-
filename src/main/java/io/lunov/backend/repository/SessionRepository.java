package io.lunov.backend.repository;

import io.lunov.backend.model.entity.Session;
import io.lunov.backend.model.entity.SessionAccessType;
import io.lunov.backend.model.entity.SessionContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session> {
    List<Session> findAllByAccessType(SessionAccessType accessType);
    List<Session> findAllByClientId(UUID clientId);
    Optional<Session> findByAccessCodeAndClientEmail(String accessCode, String clientEmail);
    Optional<Session> findByContentTypeAndAccessType(SessionContentType contentType, SessionAccessType accessType);

}
