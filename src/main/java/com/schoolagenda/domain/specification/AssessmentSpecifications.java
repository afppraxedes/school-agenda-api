package com.schoolagenda.domain.specification;

import com.schoolagenda.application.web.dto.common.assessment.AssessmentFilterRequest;
import com.schoolagenda.domain.model.Assessment;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssessmentSpecifications {

    public static Specification<Assessment> withFilters(AssessmentFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Filtro por título
            if (StringUtils.hasText(filter.getTitle())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"
                ));
            }

            // Filtro por disciplina
            if (filter.getSubjectId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("subject").get("id"),
                        filter.getSubjectId()
                ));
            }

            // Filtro por criador
            if (filter.getCreatedById() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("createdBy").get("id"),
                        filter.getCreatedById()
                ));
            }

            // Filtro por publicado
            if (filter.getPublished() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("published"),
                        filter.getPublished()
                ));
            }

            // Filtro por datas
            if (filter.getDueDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dueDate"),
                        filter.getDueDateFrom()
                ));
            }

            if (filter.getDueDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("dueDate"),
                        filter.getDueDateTo()
                ));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Specifications individuais para reuso
    public static Specification<Assessment> isPublished() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("published"));
    }

    public static Specification<Assessment> bySubject(Long subjectId) {
        return (root, query, criteriaBuilder) ->
                subjectId != null
                        ? criteriaBuilder.equal(root.get("subject").get("id"), subjectId)
                        : criteriaBuilder.conjunction();
    }

    // ESTE MÉTODO ERA PARA UTILIZAÇÃO QUANDO HAVIA O RELACIONAMENTO DE "USER" EM "ASSESSMENT"
//    public static Specification<Assessment> byCreator(Long createdById) {
//        return (root, query, criteriaBuilder) ->
//                createdById != null
//                        ? criteriaBuilder.equal(root.get("createdBy").get("id"), createdById)
//                        : criteriaBuilder.conjunction();
//    }

    public static Specification<Assessment> createdBy(String createdBy) {
        return (root, query, criteriaBuilder) ->
                createdBy != null
                        ? criteriaBuilder.equal(root.get("createdBy"), createdBy)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Assessment> dueAfter(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                date != null
                        ? criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), date)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Assessment> dueBefore(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                date != null
                        ? criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), date)
                        : criteriaBuilder.conjunction();
    }

    /**
     * Filtra Avaliações onde o ID da Disciplina (Subject) pertence à lista de IDs fornecida.
     * Esta Specification é usada para aplicar a restrição de segurança do TEACHER.
     * * @param subjectIds Lista de IDs de disciplinas lecionadas pelo professor logado.
     * @return Specification que restringe o resultado pelas disciplinas.
     */
    public static Specification<Assessment> bySubjectIdIn(List<Long> subjectIds) {

        // 1. Tratamento de lista vazia/nula: Se não houver IDs de disciplina, não deve retornar resultados.
        if (subjectIds == null || subjectIds.isEmpty()) {
            // Retorna uma Specification que sempre é falsa (disjunção sem termos)
            return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
        }

        return (root, query, criteriaBuilder) -> {
            // 2. Acessa o relacionamento 'subject' dentro da entidade Assessment
            // assessment.subject (objeto)
            Path<Long> subjectIdPath = root.get("subject").get("id");

            // 3. Cria a cláusula 'IN' no CriteriaQuery
            // WHERE assessment.subject.id IN (:subjectIds)
            return subjectIdPath.in(subjectIds);
        };
    }
}
