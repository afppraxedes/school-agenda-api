package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.ResponsibleStudent;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsibleStudentRepository extends JpaRepository<ResponsibleStudent, Long> {

    // Find by responsible
    List<ResponsibleStudent> findByResponsibleId(Long responsibleId);

    // Find by student
    List<ResponsibleStudent> findByStudentId(Long studentId);

    // Find specific relationship
    Optional<ResponsibleStudent> findByResponsibleIdAndStudentId(Long responsibleId, Long studentId);

    // Check if relationship exists
    boolean existsByResponsibleIdAndStudentId(Long responsibleId, Long studentId);

    // Find students by responsible with pagination
    @Query("SELECT rs FROM ResponsibleStudent rs JOIN FETCH rs.student WHERE rs.responsible.id = :responsibleId")
    List<ResponsibleStudent> findStudentsByResponsibleId(@Param("responsibleId") Long responsibleId);

    // Find responsibles by student
    @Query("SELECT rs FROM ResponsibleStudent rs JOIN FETCH rs.responsible WHERE rs.student.id = :studentId")
    List<ResponsibleStudent> findResponsiblesByStudentId(@Param("studentId") Long studentId);

    // Count students by responsible
    long countByResponsibleId(Long responsibleId);

    // Delete by responsible and student
    void deleteByResponsibleIdAndStudentId(Long responsibleId, Long studentId);

    // Verifica se existe o vínculo entre o ID do usuário responsável e o ID do usuário estudante
    @Query("SELECT COUNT(rs) > 0 FROM ResponsibleStudent rs " +
            "WHERE rs.responsible.id = :responsibleUserId " +
            "AND rs.student.user.id = :studentUserId")
    boolean existsByResponsibleIdAndStudentUserId(
            @Param("responsibleUserId") Long responsibleUserId,
            @Param("studentUserId") Long studentUserId);

    // Busca os vínculos carregando a entidade Student e User (Responsável) em uma única query
    @Query("SELECT rs FROM ResponsibleStudent rs " +
            "JOIN FETCH rs.student s " +
            "JOIN FETCH rs.responsible r " +
            "WHERE r.id = :responsibleUserId")
    List<ResponsibleStudent> findAllByResponsibleUserId(@Param("responsibleUserId") Long responsibleUserId);
}