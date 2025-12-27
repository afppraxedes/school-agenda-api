package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.StudentAttendanceRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.domain.model.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttendanceMapper {

    // 1. De Entidade para DTO (O que você já tem)
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.name", target = "subjectName")
    AttendanceResponse toResponse(Attendance attendance);

    // 2. De Request para Entidade (Essencial para novos registros individuais)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true) // Carregado via Repo no Service
    @Mapping(target = "subject", ignore = true) // Carregado via Repo no Service
    Attendance toEntity(StudentAttendanceRequest request);

    // 3. Update (Para editar uma falta ou presença específica)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "date", ignore = true)
    void updateEntity(StudentAttendanceRequest request, @MappingTarget Attendance attendance);
}