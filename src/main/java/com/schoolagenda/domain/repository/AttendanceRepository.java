package com.schoolagenda.domain.repository;

import com.schoolagenda.application.web.dto.common.attendance.AttendanceSummary;
import com.schoolagenda.domain.model.Attendance;
import com.schoolagenda.domain.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);

    // Conta faltas de um aluno em uma disciplina específica
    long countByStudentIdAndSubjectIdAndPresentFalse(Long studentId, Long subjectId);

    // Conta o total de aulas registradas para um aluno em uma disciplina
    long countByStudentIdAndSubjectId(Long studentId, Long subjectId);

    // Busca um registro específico para evitar duplicidade no lançamento
    Optional<Attendance> findByStudentIdAndTimetableIdAndDate(Long studentId, Long timetableId, LocalDate date);

    // Lista todas as presenças de um aluno em uma disciplina (Para o Boletim)
    List<Attendance> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    // Busca faltas de um aluno em um intervalo de datas (Para relatório de assiduidade)
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :start AND :end")
    List<Attendance> findAttendanceHistory(Long studentId, LocalDate start, LocalDate end);

    @Query("""
    SELECT new com.schoolagenda.application.web.dto.common.attendance.AttendanceSummary(
        a.timetable.teacherClass.subject.id, 
        COUNT(a.id), 
        CAST(SUM(CASE WHEN a.present = false THEN 1 ELSE 0 END) AS long)
    )
        FROM Attendance a
        WHERE a.student.user.id = :studentUserId
        GROUP BY a.timetable.teacherClass.subject.id
    """)
    List<AttendanceSummary> findAttendanceSummariesByStudent(@Param("studentUserId") Long studentUserId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.present = true")
    long countPresentDays(Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalDays(Long studentId);
}
