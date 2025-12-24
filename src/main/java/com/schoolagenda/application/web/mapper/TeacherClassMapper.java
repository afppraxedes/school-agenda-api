package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.domain.model.TeacherClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherClassMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    TeacherClass toEntity(TeacherClassRequest request);

    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.name", target = "teacherName")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(source = "schoolClass.id", target = "schoolClassId")
    @Mapping(source = "schoolClass.name", target = "schoolClassName")
    TeacherClassResponse toResponse(TeacherClass teacherClass);

    // ADICIONADO: Método de atualização de entidade existente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    void updateEntity(TeacherClassRequest request, @MappingTarget TeacherClass teacherClass);
}