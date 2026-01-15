package io.lunov.backend.repository;

import io.lunov.backend.model.entity.Photo;
import io.lunov.backend.model.entity.SessionAccessType;
import io.lunov.backend.model.entity.SessionContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    @Query("SELECT p FROM Photo p JOIN p.sessions s WHERE s.id = :sessionId")
    List<Photo> findBySessionId(UUID sessionId);

    @Query("SELECT p FROM Photo p WHERE p.primarySession.id = :sessionId")
    List<Photo> findByPrimarySessionId(UUID sessionId);

    @Query("SELECT p FROM Photo p JOIN p.sessions s WHERE s.contentType = :contentType AND s.accessType = :accessType")
    List<Photo> findByPortfolio(SessionContentType contentType, SessionAccessType accessType);

}
