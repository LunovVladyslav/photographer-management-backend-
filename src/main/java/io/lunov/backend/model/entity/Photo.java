package io.lunov.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "photos",
        indexes = {
                @Index(name = "idx_filename", columnList = "filename"),
        }
)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String originalUrl;
    private String previewUrl;
    private String filename;
    @ManyToMany(mappedBy = "photos")
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "primary_session_id")
    private Session primarySession;
    @CreationTimestamp
    private Instant createdAt;
}
