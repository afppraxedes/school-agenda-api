package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.domain.model.Grade;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AssessmentMapper.class, UserMapper.class})
public interface GradeMapper {

    @Mapping(source = "assessmentId", target = "assessment.id")
    @Mapping(source = "studentUserId", target = "student.id")
    @Mapping(source = "gradedByUserId", target = "gradedBy.id")
    @Mapping(target = "score", qualifiedByName = "gradeScaleBigDecimal")
    Grade toEntity(GradeRequest request);

    GradeResponse toResponse(Grade grade);

    @Mapping(source = "assessmentId", target = "assessment.id")
    @Mapping(source = "studentUserId", target = "student.id")
    @Mapping(source = "gradedByUserId", target = "gradedBy.id")
    @Mapping(target = "score", qualifiedByName = "gradeScaleBigDecimal")
    void updateEntity(GradeRequest request, @MappingTarget Grade grade);

    @Named("gradeScaleBigDecimal")
    default BigDecimal gradeScaleBigDecimal(BigDecimal value) {
        return scaleBigDecimal(value);
    }

    // Método privado auxiliar (não precisa de @Named)
    private BigDecimal scaleBigDecimal(BigDecimal value) {
        if (value == null) return null;
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}