package com.schoolagenda.application.web.mapper;

import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeDetailResponse;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.domain.model.Grade;
import com.schoolagenda.domain.model.SchoolClass;
import com.schoolagenda.domain.model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AssessmentMapper.class, UserMapper.class})
public interface GradeMapper {

    // Para criação - ignore todos os campos gerenciados automaticamente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gradedBy", ignore = true) // Definido manualmente no Service
    @Mapping(target = "assessment", ignore = true) // Definido manualmente no Service
    @Mapping(target = "student", ignore = true) // Definido manualmente no Service
    @Mapping(target = "score", qualifiedByName = "gradeScaleBigDecimal")
    Grade toEntity(GradeRequest request);

    GradeResponse toResponse(Grade grade);

    // No GradeMapper (MapStruct)
    @Mapping(source = "assessment.title", target = "assessmentTitle")
    @Mapping(source = "assessment.weight", target = "weight")
    @Mapping(source = "score", target = "score")
    GradeDetailResponse toDetailResponse(Grade grade);

    @Mapping(target = "id", ignore = true)           // ID nunca muda
    @Mapping(target = "createdBy", ignore = true)    // Quem criou nunca muda
    @Mapping(target = "createdAt", ignore = true)    // Data de criação nunca muda
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "assessment", ignore = true)
    @Mapping(target = "gradedBy", ignore = true)
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