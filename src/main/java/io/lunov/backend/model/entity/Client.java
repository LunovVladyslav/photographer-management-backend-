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
        name = "clients",
        indexes = {
                @Index(name = "idx_name", columnList = "name"),
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_phone_number", columnList = "phoneNumber")
        }
)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();
    @CreationTimestamp
    private Instant createdAt;
}
