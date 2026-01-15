package io.lunov.backend.util.mapper;

import io.lunov.backend.model.dto.photo.PhotoInfoDTO;
import io.lunov.backend.model.entity.Photo;
import io.lunov.backend.model.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    @Mapping(target = "sessionIds", source = "sessions")
    @Mapping(target = "primarySessionId", source = "primarySession.id")
    PhotoInfoDTO toDto(Photo photo);

    default List<UUID> mapSessions(List<Session> sessions) {
        if (sessions == null) {
            return List.of();
        }
        return sessions.stream()
                .map(Session::getId)
                .toList();
    }
}
