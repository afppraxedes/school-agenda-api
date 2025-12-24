package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.SchoolClassRequest;
import com.schoolagenda.application.web.dto.response.SchoolClassResponse;
import com.schoolagenda.domain.model.SchoolClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class}
)
public interface SchoolClassMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "coordinator", ignore = true)
    SchoolClass toEntity(SchoolClassRequest request);

    @Mapping(source = "coordinator.id", target = "coordinatorId")
    SchoolClassResponse toResponse(SchoolClass schoolClass);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "lastModifiedBy", ignore = true) // Adicione esta linha
//    @Mapping(target = "updatedAt", ignore = true) // Adicione esta linha
    @Mapping(target = "coordinator", ignore = true)
    void updateEntity(@MappingTarget SchoolClass schoolClass, SchoolClassRequest request);
}