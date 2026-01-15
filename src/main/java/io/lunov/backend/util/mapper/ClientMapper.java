package io.lunov.backend.util.mapper;

import io.lunov.backend.model.dto.client.ClientDTO;
import io.lunov.backend.model.dto.client.ClientInfoDTO;
import io.lunov.backend.model.entity.Client;
import io.lunov.backend.model.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "sessions", target = "sessions", qualifiedByName = "sessionsToUUIDs")
    ClientInfoDTO toInfoDTO(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Client toEntity(ClientDTO dto);

    @Named("sessionsToUUIDs")
    default List<UUID> sessionsToUUIDs(List<Session> sessions) {
        if (sessions == null) {
            return List.of();
        }
        return sessions.stream()
                .map(Session::getId)
                .toList();
    }
}