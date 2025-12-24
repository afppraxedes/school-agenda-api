package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.AnnouncementRequest;
import com.schoolagenda.application.web.dto.response.AnnouncementResponse;
import com.schoolagenda.domain.model.Announcement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AnnouncementMapper {

    // Para criação - ignore todos os campos gerenciados automaticamente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Announcement toEntity(AnnouncementRequest request);

    // Mapeamento para response - não precisa de configurações especiais
    // O MapStruct vai automaticamente mapear os campos herdados
    AnnouncementResponse toResponse(Announcement announcement);

    // Para atualização - NÃO tente mapear campos que não existem no request!
    // O MapStruct mapeia automaticamente os campos que têm o mesmo nome
    @Mapping(target = "id", ignore = true)           // ID nunca muda
    @Mapping(target = "createdBy", ignore = true)    // Quem criou nunca muda
    @Mapping(target = "createdAt", ignore = true)    // Data de criação nunca muda
    // lastModifiedBy e updatedAt serão atualizados automaticamente pelo Spring
    void updateEntity(@MappingTarget Announcement announcement, AnnouncementRequest request);
}
