package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.TimetableRequest;
import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.domain.model.Timetable;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimetableMapper {

    @Mapping(source = "teacherClass.id", target = "teacherClassId")
    @Mapping(source = "teacherClass.teacher.name", target = "teacherName")
    @Mapping(source = "teacherClass.subject.name", target = "subjectName")
    @Mapping(source = "teacherClass.schoolClass.name", target = "schoolClassName")
    TimetableResponse toResponse(Timetable timetable);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherClass", ignore = true) // Tratado no Service via ID
    Timetable toEntity(TimetableRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherClass", ignore = true) // Tratado no Service via ID
    void updateEntity(TimetableRequest request, @MappingTarget Timetable timetable);
}
