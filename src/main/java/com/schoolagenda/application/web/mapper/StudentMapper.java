package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.StudentRequest;
import com.schoolagenda.application.web.dto.response.SchoolClassSimpleResponse;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.SchoolClass;
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
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    // Mapeia o Long para o ID da entidade aninhada
    @Mapping(target = "schoolClass.id", source = "schoolClassId")
    Student toEntity(StudentRequest studentRequest);

    // MapStruct 1.5+ detecta automaticamente o construtor do Record StudentResponse
    @Mapping(target = "schoolClass", source = "schoolClass")
    StudentResponse toResponse(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "schoolClass.id", source = "schoolClassId")
    void updateEntity(StudentRequest request, @MappingTarget Student student);

    // Método utilitário para converter a entidade SchoolClass no DTO de resposta
    default SchoolClassSimpleResponse mapSchoolClass(SchoolClass schoolClass) {
        if (schoolClass == null) return null;
        return new SchoolClassSimpleResponse(
                schoolClass.getId(),
                schoolClass.getName(),
                schoolClass.getDescription(),
                schoolClass.getYear()
        );
    }
}