package com.schoolagenda.domain.specification;

import com.schoolagenda.application.web.dto.common.subject.SubjectFilterRequest;
import com.schoolagenda.domain.model.Subject;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SubjectSpecifications {

    public static Specification<Subject> withFilters(SubjectFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            // Se não tem filtro, retorna todas (não filtra)
            if (filter == null) {
                return criteriaBuilder.conjunction(); // WHERE 1=1
            }

            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nome (case-insensitive, like)
            if (StringUtils.hasText(filter.getName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            // Filtro por ano letivo (exato)
            if (StringUtils.hasText(filter.getSchoolYear())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("schoolYear"),
                        filter.getSchoolYear()
                ));
            }

            // Filtro por professor
            if (filter.getTeacherId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("teacher").get("id"),
                        filter.getTeacherId()
                ));
            }

            // Filtro por ativo/inativo
            if (filter.getActive() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("active"),
                        filter.getActive()
                ));
            }

            // Se não há predicados, retorna todas
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Specifications individuais atualizadas
    public static Specification<Subject> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

    public static Specification<Subject> bySchoolYear(String schoolYear) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(schoolYear)
                        ? criteriaBuilder.equal(root.get("schoolYear"), schoolYear)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Subject> byTeacher(Long teacherId) {
        return (root, query, criteriaBuilder) ->
                teacherId != null
                        ? criteriaBuilder.equal(root.get("teacher").get("id"), teacherId)
                        : criteriaBuilder.conjunction();
    }
}