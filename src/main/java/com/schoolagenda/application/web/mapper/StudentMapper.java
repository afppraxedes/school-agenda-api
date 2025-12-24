package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.StudentRequest;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.domain.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(source = "user", target = "user.id")
//    @Mapping(source = "schoolClass", target = "schoolClass.id")
    Student toEntity(StudentRequest studentRequest);

    StudentResponse toResponse(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(source = "user", target = "user.id")
//    @Mapping(source = "schoolClass", target = "schoolClass.id")
    void updateEntity(StudentRequest request, @MappingTarget Student student);

}
