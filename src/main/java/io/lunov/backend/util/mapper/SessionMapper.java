package io.lunov.backend.util.mapper;

import io.lunov.backend.model.dto.photo.PhotoShortInfoDTO;
import io.lunov.backend.model.dto.session.SessionCreateDTO;
import io.lunov.backend.model.dto.session.SessionInfoDTO;
import io.lunov.backend.model.entity.Client;
import io.lunov.backend.model.entity.Photo;
import io.lunov.backend.model.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "accessCode", ignore = true)
    @Mapping(target = "client", source = "clientId",qualifiedByName = "clientIdToClient")
    @Mapping(source = "sessionDate", target = "sessionDate", qualifiedByName = "stringToInstant")
    Session toEntity(SessionCreateDTO dto);

    @Mapping(source = "photos", target = "photos", qualifiedByName = "photosToUrls")
    @Mapping(target = "clientId", source = "client.id")
    SessionInfoDTO toDTO(Session session);

    @Named("stringToInstant")
    default Instant stringToInstant(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString)
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant();
    }

    @Named("photosToUrls")
    default List<PhotoShortInfoDTO> photosToDTO(List<Photo> photos) {
        if (photos == null) {
            return List.of();
        }
        return photos.stream()
                .map(photo -> PhotoShortInfoDTO.builder()
                        .id(photo.getId())
                        .originalUrl(photo.getOriginalUrl())
                        .previewUrl(photo.getPreviewUrl())
                        .build())
                .toList();
    }

    @Named("clientIdToClient")
    default Client clientIdToClient(UUID clientId) {
        if (clientId == null) {
            return new Client();
        }
        return Client.builder()
                .id(clientId)
                .build();
    }
}
