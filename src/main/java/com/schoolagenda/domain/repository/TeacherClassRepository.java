// src/main/java/com/schoolagenda/domain/repository/TeacherClassRepository.java
package com.schoolagenda.domain.repository;

import com.schoolagenda.application.web.dto.GradeStudentDTO;
import com.schoolagenda.domain.model.TeacherClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherClassRepository extends JpaRepository<TeacherClass, Long> {

    // Find all classes by teacher
    List<TeacherClass> findByTeacherId(Long teacherId);

    // Find all classes by school class
    List<TeacherClass> findBySchoolClassId(Long schoolClassId);


    // Find specific by teacher, subject and school class
    Optional<TeacherClass> findByTeacherIdAndSubjectIdAndSchoolClassId(
            Long teacherId, Long subjectId, Long schoolClassId);

    // Find all classes with teacher details
    @Query("SELECT tc FROM TeacherClass tc JOIN FETCH tc.teacher WHERE tc.teacher.id = :teacherId")
    List<TeacherClass> findClassesWithTeacherByTeacherId(@Param("teacherId") Long teacherId);

    // Count classes by teacher
    long countByTeacherId(Long teacherId);

    // Find distinct school class IDs
    @Query("SELECT DISTINCT tc.schoolClass.id FROM TeacherClass tc ORDER BY tc.schoolClass.id")
    List<Long> findDistinctSchoolClassIds();

    // Delete by teacher, subject and school class
    void deleteByTeacherIdAndSubjectIdAndSchoolClassId(Long teacherId, Long subjectId, Long schoolClassId);

    // NOVO: Retorna os IDs das disciplinas (Subject) lecionadas por um professor
    // Assumindo que a entidade TeacherClass tem um link para Subject
    // Se TeacherClass só tem nome da turma, essa query é mais complexa.
    // Vamos assumir uma relação TeacherClass -> Subject (via a turma ou diretamente)
    // Método auxiliar útil para o filtro de segurança do Professor
    @Query("SELECT DISTINCT tc.subject.id FROM TeacherClass tc WHERE tc.teacher.id = :teacherId")
    List<Long> findSubjectIdsByTeacherId(@Param("teacherId") Long teacherId);

    // Busca os IDs dos estudantes vinculados ao ID do usuário Responsável
    // Assume-se que na sua entidade Student existe um @ManyToOne ou @ManyToMany com Responsible
    @Query("SELECT s.id FROM Student s WHERE s.user.id = :responsibleId")
    List<Long> findStudentIdsByResponsibleId(@Param("responsibleId") Long responsibleId);

    // Validação booleana do vínculo Professor/Disciplina/Turma verificando se o relacionamento existe
    @Query("SELECT COUNT(tc) > 0 FROM TeacherClass tc " +
            "WHERE tc.teacher.id = :teacherId " +
            "AND tc.subject.id = :subjectId " +
            "AND tc.schoolClass.id = :schoolClassId")
    boolean existsByTeacherIdAndSubjectIdAndSchoolClassId(
            @Param("teacherId") Long teacherId,
            @Param("subjectId") Long subjectId,
            @Param("schoolClassId") Long schoolClassId);

    /**
     * Verifica se o professor leciona em QUALQUER disciplina para a turma do aluno.
     * Útil para autorizar a visualização do boletim completo.
     */
    @Query("""
        SELECT COUNT(tc) > 0 
        FROM TeacherClass tc 
        JOIN Student s ON s.schoolClass.id = tc.schoolClass.id 
        WHERE tc.teacher.id = :teacherUserId 
        AND s.user.id = :studentUserId
    """)
    boolean existsTeacherLinkWithStudent(
            @Param("teacherUserId") Long teacherUserId,
            @Param("studentUserId") Long studentUserId);

    // Removido o filtro 'active'
    @Query("SELECT tc FROM TeacherClass tc WHERE tc.teacher.email = :email")
    List<TeacherClass> findByTeacherEmail(@Param("email") String email);

    /**
     * Busca otimizada de alunos e notas utilizando projeção para DTO (Record).
     * Esta consulta resolve o problema de N+1 e garante alta performance no dashboard docente.
     */
    @Query("""
        SELECT new com.schoolagenda.application.web.dto.GradeStudentDTO(
            s.user.id, 
            s.fullName, 
            g.score, 
            g.feedback
        )
        FROM Student s
        LEFT JOIN Grade g ON g.student.id = s.user.id 
            AND g.assessment.teacherClass.id = :teacherClassId
        WHERE s.schoolClass.id = (
            SELECT tc.schoolClass.id 
            FROM TeacherClass tc 
            WHERE tc.id = :teacherClassId
        )
        ORDER BY s.fullName ASC
    """)
    List<GradeStudentDTO> findStudentsGradesByTeacherClassId(@Param("teacherClassId") Long teacherClassId);
}