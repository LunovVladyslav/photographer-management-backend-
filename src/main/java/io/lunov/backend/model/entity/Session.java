package io.lunov.backend.model.entity;

import io.lunov.backend.model.validation.AccessCode;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
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
        name = "sessions",
        indexes = {
                @Index(name = "idx_session_name", columnList = "name"),
        }
)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "sessions_photos",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "photo_id")
    )
    @Builder.Default
    private List<Photo> photos = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type")
    private SessionAccessType accessType;
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private SessionContentType contentType;
    @Column(name = "access_code", unique = true, length = 20)
    @AccessCode
    @Valid
    private String accessCode;
    @PastOrPresent
    @Column(name = "session_date", updatable = false)
    private Instant sessionDate;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    public void addPhoto(Photo photo) {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photos.add(photo);
        photo.getSessions().add(this);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.getSessions().remove(this);
    }

    public void removeAllPhotos() {
        photos.forEach(photo -> {
            photo.getSessions().remove(this);
            if (photo.getPrimarySession() != null &&
                    photo.getPrimarySession().getId().equals(this.id)) {
                photo.setPrimarySession(null);
            }
        });
        photos.clear();
    }
}
