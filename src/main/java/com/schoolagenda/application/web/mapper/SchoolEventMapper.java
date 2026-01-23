package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.SchoolEventRequest;
import com.schoolagenda.application.web.dto.response.SchoolEventResponse;
import com.schoolagenda.domain.model.SchoolEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolEventMapper {

    @Mapping(source = "schoolClass.id", target = "schoolClassId")
    @Mapping(source = "schoolClass.name", target = "schoolClassName")
    SchoolEventResponse toResponse(SchoolEvent entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true) // Tratado manualmente no Service
    SchoolEvent toEntity(SchoolEventRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    void updateEntity(SchoolEventRequest request, @MappingTarget SchoolEvent entity);
}
