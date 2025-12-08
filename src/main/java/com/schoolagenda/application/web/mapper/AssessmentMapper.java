package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.domain.model.Assessment;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {SubjectMapper.class, UserMapper.class})
public interface AssessmentMapper {

    @Mapping(source = "subjectId", target = "subject.id")
    @Mapping(source = "createdByUserId", target = "createdBy.id")
    @Mapping(target = "maxScore", qualifiedByName = "assessmentScaleBigDecimal")
    Assessment toEntity(AssessmentRequest request);

    AssessmentResponse toResponse(Assessment assessment);

    @Mapping(source = "subjectId", target = "subject.id")
    @Mapping(source = "createdByUserId", target = "createdBy.id")
    @Mapping(target = "maxScore", qualifiedByName = "assessmentScaleBigDecimal")
    void updateEntity(AssessmentRequest request, @MappingTarget Assessment assessment);

    @Named("assessmentScaleBigDecimal")
    default BigDecimal assessmentScaleBigDecimal(BigDecimal value) {
        return scaleBigDecimal(value);
    }

    // Método privado auxiliar (não precisa de @Named)
    private BigDecimal scaleBigDecimal(BigDecimal value) {
        if (value == null) return null;
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}