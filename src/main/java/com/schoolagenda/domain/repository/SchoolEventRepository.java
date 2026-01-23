package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.SchoolEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolEventRepository extends JpaRepository<SchoolEvent, Long> {

    // Busca eventos da escola toda ou de uma turma específica
    @Query("""
        SELECT e FROM SchoolEvent e 
        WHERE (e.schoolClass.id IS NULL OR e.schoolClass.id = :classId)
        AND e.startDate BETWEEN :start AND :end
        ORDER BY e.startDate ASC
    """)
    List<SchoolEvent> findByClassAndDateRange(
            @Param("classId") Long classId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);

    // Busca apenas eventos globais (feriados, etc)
    List<SchoolEvent> findBySchoolClassIdIsNullAndStartDateBetween(OffsetDateTime start, OffsetDateTime end);

    // Busca todos os eventos por período (para Admins)
    List<SchoolEvent> findAllByStartDateBetweenOrderByStartDateAsc(OffsetDateTime start, OffsetDateTime end);
}
