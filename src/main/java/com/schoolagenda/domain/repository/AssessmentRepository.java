package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long>,
        JpaSpecificationExecutor<Assessment> {

    List<Assessment> findBySubjectId(Long subjectId);

    List<Assessment> findBySubjectIdAndPublishedTrue(Long subjectId);

    // TODO: AJUSTAR ESSA QUERY, POIS FOI REMOVIDO O "USER" DA ENTIDADE "ASSESSMENT"!
    // A PROPRIEDADE "createdById" NÃO ESTÁ MAIS PRESENTE NA ENTIDADE "ASSESSMENT"!
//    List<Assessment> findByCreatedById(Long createdById);

    List<Assessment> findByPublishedTrue();

    List<Assessment> findByPublishedTrueAndSubjectId(Long subjectId);
    List<Assessment> findByPublishedTrueAndDueDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Assessment a WHERE a.subject.id = :subjectId AND a.published = true " +
            "ORDER BY a.dueDate ASC NULLS LAST")
    List<Assessment> findPublishedBySubjectOrderByDueDate(@Param("subjectId") Long subjectId);

//    @Query("SELECT a FROM Assessment a WHERE a.published = true AND " +
//            "(:subjectId IS NULL OR a.subject.id = :subjectId) AND " +
//            "(:startDate IS NULL OR a.dueDate >= :startDate) AND " +
//            "(:endDate IS NULL OR a.dueDate <= :endDate) " +
//            "ORDER BY a.dueDate ASC")
//    List<Assessment> findPublishedByFilters(
//            @Param("subjectId") Long subjectId,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Assessment a WHERE a.published = true AND " +
            "(:subjectId IS NULL OR a.subject.id = :subjectId) AND " +
            "(:startDate IS NULL OR a.dueDate >= CAST(:startDate AS date)) AND " +
            "(:endDate IS NULL OR a.dueDate <= CAST(:endDate AS date)) " +
            "ORDER BY a.dueDate ASC")
    List<Assessment> findPublishedByFilters(
            @Param("subjectId") Long subjectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT a.* FROM assessments a " +
            "WHERE a.is_published = true " +
            "AND (:subjectId IS NULL OR a.subject_id = :subjectId) " +
            "AND (:startDate IS NULL OR a.due_date >= CAST(:startDate AS DATE)) " +
            "AND (:endDate IS NULL OR a.due_date <= CAST(:endDate AS DATE)) " +
            "ORDER BY a.due_date ASC",
            nativeQuery = true)
    List<Assessment> findPublishedByFiltersNative(
            @Param("subjectId") Long subjectId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // TODO: AJUSTAR ESSA QUERY, POIS FOI REMOVIDO O "USER" DA ENTIDADE "ASSESSMENT"!
    // INSTRUÇÃO ANTERIOR:
//    @Query("SELECT a FROM Assessment a WHERE a.createdBy.id = :teacherId AND " +
//            "a.subject.id = :subjectId AND a.published = true")

    // INSTRUÇÃO APÓS ALTERAÇÃO (SOMENTE PARA NÃO DAR ERRO AO SUBIR A APLICAÇÃO)
    @Query("SELECT a FROM Assessment a WHERE a.id = :teacherId AND " +
            "a.subject.id = :subjectId AND a.published = true")
    List<Assessment> findByTeacherAndSubject(
            @Param("teacherId") Long teacherId,
            @Param("subjectId") Long subjectId);

    @Query("SELECT a FROM Assessment a JOIN FETCH a.subject JOIN FETCH a.createdBy WHERE a.id = :id")
    Optional<Assessment> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.subject.id = :subjectId")
    long countBySubjectId(@Param("subjectId") Long subjectId);
}
