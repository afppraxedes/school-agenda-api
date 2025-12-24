package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.SubjectRequest;
import com.schoolagenda.application.web.dto.response.SubjectResponse;
import com.schoolagenda.application.web.dto.response.SubjectSimpleResponse;
import com.schoolagenda.domain.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubjectMapper {

    Subject toEntity(SubjectRequest request);

    SubjectResponse toResponse(Subject subject);

    SubjectSimpleResponse toSimpleResponse(Subject subject);

    void updateEntity(SubjectRequest request, @MappingTarget Subject subject);
}
