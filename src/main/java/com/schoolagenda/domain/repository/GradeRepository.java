package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long>,
        JpaSpecificationExecutor<Grade> {

    Optional<Grade> findByAssessmentIdAndStudentId(Long assessmentId, Long studentId);

    List<Grade> findByAssessmentId(Long assessmentId);

    List<Grade> findByStudentId(Long studentId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.assessment.subject.id = :subjectId")
    List<Grade> findByStudentIdAndSubjectId(@Param("studentId") Long studentId,
                                            @Param("subjectId") Long subjectId);

    @Query("SELECT g FROM Grade g WHERE g.assessment.id = :assessmentId AND g.score IS NULL")
    List<Grade> findUngradedByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("SELECT g FROM Grade g JOIN FETCH g.assessment JOIN FETCH g.student WHERE g.id = :id")
    Optional<Grade> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.assessment.id = :assessmentId")
    long countByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("SELECT AVG(g.score) FROM Grade g WHERE " +
            "g.student.id = :studentId AND g.assessment.subject.id = :subjectId AND g.score IS NOT NULL")
    Double calculateAverageByStudentAndSubject(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId);

    /**
     * Busca todas as notas de um estudante específico.
     * O 'JOIN FETCH' garante que a Avaliação e a Disciplina venham na mesma consulta,
     * tornando o cálculo do boletim muito mais rápido.
     */
    @Query("SELECT g FROM Grade g " +
            "JOIN FETCH g.assessment a " +
            "JOIN FETCH a.subject " +
            "WHERE g.student.id = :studentUserId")
    List<Grade> findAllByStudentId(@Param("studentUserId") Long studentUserId);

    boolean existsByAssessmentIdAndStudentId(Long assessmentId, Long studentId);
}
