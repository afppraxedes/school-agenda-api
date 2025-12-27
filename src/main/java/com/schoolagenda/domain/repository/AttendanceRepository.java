package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);

    // Método que usaremos no Boletim para contar faltas
    long countByStudentIdAndSubjectIdAndPresentFalse(Long studentId, Long subjectId);

    // Total de aulas dadas na disciplina para o aluno
    long countByStudentIdAndSubjectId(Long studentId, Long subjectId);

}
