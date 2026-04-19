package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long>, JpaSpecificationExecutor<Grade> {

    // Mantemos o padrão de nomenclatura consistente
    Optional<Grade> findByStudentIdAndAssessmentId(Long studentId, Long assessmentId);

    List<Grade> findByAssessmentId(Long assessmentId);

    List<Grade> findByStudentId(Long studentId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.assessment.subject.id = :subjectId")
    List<Grade> findByStudentIdAndSubjectId(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT g FROM Grade g WHERE g.assessment.id = :assessmentId AND g.score IS NULL")
    List<Grade> findUngradedByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("SELECT g FROM Grade g JOIN FETCH g.assessment JOIN FETCH g.student WHERE g.id = :id")
    Optional<Grade> findByIdWithDetails(@Param("id") Long id);

    /**
     * UNIFICAÇÃO DE TIPAGEM: Usamos BigDecimal para todas as médias.
     * COALESCE garante que retorne 0 em vez de null caso não haja notas.
     */
    @Query("SELECT COALESCE(AVG(g.score), 0) FROM Grade g WHERE " +
            "g.student.id = :studentId AND g.assessment.subject.id = :subjectId AND g.score IS NOT NULL")
    BigDecimal calculateAverageByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT g FROM Grade g JOIN FETCH g.assessment a JOIN FETCH a.subject WHERE g.student.id = :studentUserId")
    List<Grade> findAllByStudentId(@Param("studentUserId") Long studentUserId);

    boolean existsByAssessmentIdAndStudentId(Long assessmentId, Long studentId);

    @Query("SELECT COALESCE(AVG(g.score), 0) FROM Grade g WHERE g.student.id = :userId")
    BigDecimal calculateAverageByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :userId AND g.assessment.subject.id = :subjectId ORDER BY g.gradedAt DESC")
    List<Grade> findTop2ByStudentIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);

    /**
     * Média Ponderada: SUM(Nota * Peso) / SUM(Pesos)
     */
    @Query("SELECT COALESCE(SUM(g.score * a.weight) / SUM(a.weight), 0) " +
            "FROM Grade g JOIN g.assessment a " +
            "WHERE g.student.id = :userId AND g.score IS NOT NULL")
    BigDecimal calculateWeightedAverage(@Param("userId") Long userId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :userId " +
            "AND g.assessment.subject.id = :subjectId " +
            "ORDER BY g.assessment.dueDate ASC")
    List<Grade> findTop4ByStudentIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId " +
            "AND g.assessment.teacherClass.id = :teacherClassId " +
            "ORDER BY g.assessment.dueDate ASC")
    List<Grade> findGradesForTeacherClass(
            @Param("studentId") Long studentId,
            @Param("teacherClassId") Long teacherClassId
    );
}