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

    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.student.id = :userId")
    Double calculateAverageByStudentUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.student.id = :userId")
    BigDecimal findAverageByStudentUserId(@Param("userId") Long userId);

    // Busca as 2 últimas notas do aluno em uma disciplina específica
    @Query("SELECT g FROM Grade g WHERE g.student.id = :userId AND g.assessment.subject.id = :subjectId ORDER BY g.gradedAt DESC")
    List<Grade> findTop2ByStudentIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);

    // Média Ponderada: SUM(Nota * Peso) / SUM(Pesos)
    /**
     * Média Ponderada: SUM(Nota * Peso) / SUM(Pesos)
     * Filtramos 'score IS NOT NULL' para não distorcer a média com avaliações pendentes.
     */
    @Query("SELECT SUM(g.score * a.weight) / SUM(a.weight) " +
            "FROM Grade g JOIN g.assessment a " +
            "WHERE g.student.id = :userId AND g.score IS NOT NULL")
    BigDecimal calculateWeightedAverage(@Param("userId") Long userId);

    /**
     * Busca as 4 notas do aluno ordenadas pela data da avaliação.
     */
    @Query("SELECT g FROM Grade g WHERE g.student.id = :userId " +
            "AND g.assessment.subject.id = :subjectId " +
            "ORDER BY g.assessment.dueDate ASC")
    List<Grade> findTop4ByStudentIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);

    // Método solicitado para o histórico (Evolution Chart)
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId " +
            "ORDER BY g.assessment.dueDate ASC")
    List<Grade> findTop4ByStudentIdOrderByDate(@Param("studentId") Long studentId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId " +
            "ORDER BY g.assessment.dueDate ASC")
    List<Grade> findAllByStudentIdOrderByDate(@Param("studentId") Long studentId);
}
