package com.schoolagenda.domain.specification;

import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.domain.model.Grade;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GradeSpecifications {

    public static Specification<Grade> withFilters(GradeFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Filtro por avaliação
            if (filter.getAssessmentId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("assessment").get("id"),
                        filter.getAssessmentId()
                ));
            }

            // Filtro por estudante
            if (filter.getStudentId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("student").get("id"),
                        filter.getStudentId()
                ));
            }

            // Filtro por quem avaliou
            if (filter.getGradedById() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("gradedBy").get("id"),
                        filter.getGradedById()
                ));
            }

            // Filtro por disciplina (via join com assessment)
            if (filter.getSubjectId() != null) {
                Join<Object, Object> assessmentJoin = root.join("assessment");
                predicates.add(criteriaBuilder.equal(
                        assessmentJoin.get("subject").get("id"),
                        filter.getSubjectId()
                ));
            }

            // Filtro por intervalo de nota
            if (filter.getMinScore() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("score"),
                        filter.getMinScore()
                ));
            }

            if (filter.getMaxScore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("score"),
                        filter.getMaxScore()
                ));
            }

            // Filtro por ausente
            if (filter.getAbsent() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("absent"),
                        filter.getAbsent()
                ));
            }

            // Filtro por justificada
            if (filter.getExcused() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("excused"),
                        filter.getExcused()
                ));
            }

            // Filtro por "já avaliado" (score != null)
            if (filter.getGraded() != null) {
                if (Boolean.TRUE.equals(filter.getGraded())) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("score")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("score")));
                }
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ========== SPECIFICATIONS INDIVIDUAIS ==========

    public static Specification<Grade> byAssessment(Long assessmentId) {
        return (root, query, criteriaBuilder) ->
                assessmentId != null
                        ? criteriaBuilder.equal(root.get("assessment").get("id"), assessmentId)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Grade> byStudent(Long studentId) {
        return (root, query, criteriaBuilder) ->
                studentId != null
                        ? criteriaBuilder.equal(root.get("student").get("id"), studentId)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Grade> bySubject(Long subjectId) {
        return (root, query, criteriaBuilder) -> {
            if (subjectId == null) return criteriaBuilder.conjunction();

            Join<Object, Object> assessmentJoin = root.join("assessment");
            return criteriaBuilder.equal(assessmentJoin.get("subject").get("id"), subjectId);
        };
    }

    public static Specification<Grade> isGraded() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("score"));
    }

    public static Specification<Grade> isNotGraded() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("score"));
    }

    public static Specification<Grade> isAbsent() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("absent"));
    }

    public static Specification<Grade> isExcused() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("excused"));
    }

    public static Specification<Grade> scoreBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return criteriaBuilder.conjunction();
            }

            if (min != null && max != null) {
                return criteriaBuilder.between(root.get("score"), min, max);
            } else if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("score"), min);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("score"), max);
            }
        };
    }

    public static Specification<Grade> isPassing(BigDecimal passingScore) {
        return (root, query, criteriaBuilder) ->
                passingScore != null
                        ? criteriaBuilder.greaterThanOrEqualTo(root.get("score"), passingScore)
                        : criteriaBuilder.conjunction();
    }
}