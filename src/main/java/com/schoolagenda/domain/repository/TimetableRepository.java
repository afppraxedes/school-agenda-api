package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // Busca todos os horários de uma turma específica
    List<Timetable> findByTeacherClass_SchoolClass_Id(Long schoolClassId);

    // Busca a agenda de um professor específico
    List<Timetable> findByTeacherClass_Teacher_Id(Long teacherId);

    // Verificação de conflito de Professor
    @Query("""
        SELECT COUNT(t) > 0 FROM Timetable t 
        WHERE t.teacherClass.teacher.id = :teacherId 
        AND t.dayOfWeek = :day 
        AND (:start < t.endTime AND :end > t.startTime)
    """)
    boolean hasTeacherConflict(Long teacherId, DayOfWeek day, LocalTime start, LocalTime end);

    // Verificação de conflito de Turma
    @Query("""
        SELECT COUNT(t) > 0 FROM Timetable t 
        WHERE t.teacherClass.schoolClass.id = :classId 
        AND t.dayOfWeek = :day 
        AND (:start < t.endTime AND :end > t.startTime)
    """)
    boolean hasClassConflict(Long classId, DayOfWeek day, LocalTime start, LocalTime end);

    // Verificação de conflito de Professor ignorando o ID atual (para Edição)
    @Query("""
        SELECT COUNT(t) > 0 FROM Timetable t 
        WHERE t.teacherClass.teacher.id = :teacherId 
        AND t.dayOfWeek = :day 
        AND (:start < t.endTime AND :end > t.startTime)
        AND t.id <> :currentId
    """)
    boolean hasTeacherConflictIgnoringId(
            @Param("teacherId") Long teacherId,
            @Param("day") DayOfWeek day,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end,
            @Param("currentId") Long currentId);

    // Verificação de conflito de Turma ignorando o ID atual (para Edição)
    @Query("""
        SELECT COUNT(t) > 0 FROM Timetable t 
        WHERE t.teacherClass.schoolClass.id = :classId 
        AND t.dayOfWeek = :day 
        AND (:start < t.endTime AND :end > t.startTime)
        AND t.id <> :currentId
    """)
    boolean hasClassConflictIgnoringId(
            @Param("classId") Long classId,
            @Param("day") DayOfWeek day,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end,
            @Param("currentId") Long currentId);

    // Busca a próxima aula do professor hoje
    @Query("""
        SELECT t FROM Timetable t 
        WHERE t.teacherClass.teacher.id = :teacherId 
        AND t.dayOfWeek = :day 
        AND t.startTime >= :now 
        ORDER BY t.startTime ASC
        LIMIT 1
    """)
    Optional<Timetable> findNextTeacherClass(Long teacherId, DayOfWeek day, LocalTime now);

    // Busca a próxima aula da turma do aluno hoje
    @Query("""
        SELECT t FROM Timetable t 
        WHERE t.teacherClass.schoolClass.id = :classId 
        AND t.dayOfWeek = :day 
        AND t.startTime >= :now 
        ORDER BY t.startTime ASC
        LIMIT 1
    """)
    Optional<Timetable> findNextStudentClass(Long classId, DayOfWeek day, LocalTime now);

    /**
     * Busca a aula atual (onde o horário de término é maior ou igual a agora)
     * ou a próxima aula do dia para um PROFESSOR.
     */
    @Query("""
        SELECT t FROM Timetable t 
        WHERE t.teacherClass.teacher.id = :teacherId 
        AND t.dayOfWeek = :day 
        AND t.endTime >= :now 
        ORDER BY t.startTime ASC
        LIMIT 1
    """)
    Optional<Timetable> findCurrentOrNextByTeacher(
            @Param("teacherId") Long teacherId,
            @Param("day") DayOfWeek day,
            @Param("now") LocalTime now);

    // Comentado em função da alteração da assinatura do método
//    @Query("""
//        SELECT t FROM Timetable t
//        WHERE t.teacherClass.teacher.id = :teacherId
//        AND t.dayOfWeek = :day
//        AND t.endTime >= :now
//        ORDER BY t.startTime ASC
//        LIMIT 1
//    """)
//    Optional<Timetable> findCurrentOrNextByTeacher(
//            @Param("teacherId") Long teacherId,
//            @Param("day") DayOfWeek day,
//            @Param("now") LocalTime now);

    /**
     * Busca a aula atual (onde o horário de término é maior ou igual a agora)
     * ou a próxima aula do dia para uma TURMA (Alunos).
     */
    @Query("""
        SELECT t FROM Timetable t 
        WHERE t.teacherClass.schoolClass.id = :classId 
        AND t.dayOfWeek = :day 
        AND t.endTime >= :now 
        ORDER BY t.startTime ASC
        LIMIT 1
    """)
    Optional<Timetable> findCurrentOrNextByClass(
            @Param("classId") Long classId,
            @Param("day") DayOfWeek day,
            @Param("now") LocalTime now);

    @Query("""
        SELECT t 
        FROM Timetable t
        JOIN Student s ON s.schoolClass.id = t.teacherClass.schoolClass.id
        WHERE s.user.id = :studentUserId
        AND t.dayOfWeek = :dayOfWeek
        ORDER BY t.startTime ASC
    """)
    List<Timetable> findTodaySchedule(
            @Param("studentUserId") Long studentUserId,
            @Param("dayOfWeek") java.time.DayOfWeek dayOfWeek);

    @Query("""
        SELECT t FROM Timetable t
        JOIN t.teacherClass tc
        WHERE tc.schoolClass.id = :classId
        AND t.dayOfWeek = :dayOfWeek
        ORDER BY t.startTime ASC
    """)
    List<Timetable> findByClassAndDay(Long classId, DayOfWeek dayOfWeek);

    @Query("""
        SELECT t FROM Timetable t 
        WHERE t.teacherClass.schoolClass.id = :classId 
        AND t.dayOfWeek = :dayOfWeek
        ORDER BY t.startTime ASC
    """)
    List<Timetable> findByClassIdAndDayOfWeek(Long classId, DayOfWeek dayOfWeek);

    // Comentado em função da alteração da assinatura do método
//    @Query("""
//        SELECT t FROM Timetable t
//        WHERE t.teacherClass.schoolClass.id = :classId
//        AND t.dayOfWeek = :day
//        AND t.endTime >= :now
//        ORDER BY t.startTime ASC
//        LIMIT 1
//    """)
//    Optional<Timetable> findCurrentOrNextByClass(
//            @Param("classId") Long classId,
//            @Param("day") DayOfWeek day,
//            @Param("now") LocalTime now);

}
