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

    // TODO: Criado pelo "Gemini" para fazer o "RBAC" a nível de objeto
    /**
     * Filtra notas por uma lista de IDs de estudantes.
     * Essencial para o perfil RESPONSIBLE (ver notas dos filhos).
     */
    public static Specification<Grade> byStudentIds(List<Long> studentIds) {
        return (root, query, cb) -> {
            if (studentIds == null || studentIds.isEmpty()) {
                return cb.disjunction();
            }
            return root.get("student").get("id").in(studentIds);
        };
    }

    // Para buscar notas de uma turma específica:
    public static Specification<Grade> bySchoolClass(Long schoolClassId) {
        return (root, query, cb) -> {
            // O caminho agora é claro: Grade -> Student -> SchoolClass -> ID
            return cb.equal(root.join("student").get("schoolClass").get("id"), schoolClassId);
        };
    }

    /**
     * Filtra notas pela Turma (SchoolClass).
     * Navega: Grade -> Student -> SchoolClass
     */
//    public static Specification<Grade> bySchoolClassId(Long classId) {
//        return (root, query, cb) -> {
//            if (classId == null) return null;
//
//            // Faz o Join: Grade -> Student
//            Join<Object, Object> studentJoin = root.join("student");
//
//            // Faz o Join: Student -> SchoolClass (Assumindo que Student tem o atributo schoolClass)
//            // Se o seu Student for a própria entidade User, o join seria root.join("student").join("schoolClass")
//            return cb.equal(studentJoin.get("schoolClass").get("id"), classId);
//        };
//    }

    /**
     * Filtra notas pelas Disciplinas (Subjects).
     * Navega: Grade -> Assessment -> Subject
     */
    public static Specification<Grade> bySubjectIds(List<Long> subjectIds) {
        return (root, query, cb) -> {
            if (subjectIds == null || subjectIds.isEmpty()) {
                return cb.disjunction();
            }
            return root.join("assessment").join("subject").get("id").in(subjectIds);
        };
    }
}