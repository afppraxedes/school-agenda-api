package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by date range
    List<Event> findByStartDateBetweenOrderByStartDateAsc(LocalDateTime start, LocalDateTime end);

    List<Event> findByEndDateBetweenOrderByStartDateAsc(LocalDateTime start, LocalDateTime end);

    // Find events after a specific date (generic)
    List<Event> findByStartDateAfterOrderByStartDateAsc(LocalDateTime date);

    // Find events before a specific date
    List<Event> findByEndDateBeforeOrderByStartDateAsc(LocalDateTime date);

    // Find upcoming events (start date in future) - CORREÇÃO: método específico para eventos futuros
    @Query("SELECT e FROM Event e WHERE e.startDate > :now AND e.endDate > :now ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    // Find ongoing events (current date between start and end)
    @Query("SELECT e FROM Event e WHERE e.startDate <= :now AND e.endDate >= :now ORDER BY e.startDate ASC")
    List<Event> findOngoingEvents(@Param("now") LocalDateTime now);

    // Find past events (end date in past)
    List<Event> findByEndDateBeforeOrderByStartDateDesc(LocalDateTime now);

    // Find events by title containing (search)
    List<Event> findByTitleContainingIgnoreCaseOrderByStartDateAsc(String title);

    // Find events in a specific month
    @Query("SELECT e FROM Event e WHERE YEAR(e.startDate) = :year AND MONTH(e.startDate) = :month ORDER BY e.startDate ASC")
    List<Event> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Find events for today
    @Query("SELECT e FROM Event e WHERE (e.startDate BETWEEN :startOfDay AND :endOfDay) OR (e.endDate BETWEEN :startOfDay AND :endOfDay) OR (e.startDate <= :startOfDay AND e.endDate >= :endOfDay) ORDER BY e.startDate ASC")
    List<Event> findTodayEvents(@Param("startOfDay") LocalDateTime startOfDay,
                                @Param("endOfDay") LocalDateTime endOfDay);

    // TODO: O MÉTODO COMENTADO ABAIXO É UMA ALTERNATIVA AO MÉTODO ACIMA SEM PASSAR "DATA INICIAL E FINAL"
    // Alternativa 1: Método mais direto
//    @Query("SELECT e FROM Event e WHERE FUNCTION('DATE', e.startDate) = CURRENT_DATE OR FUNCTION('DATE', e.endDate) = CURRENT_DATE ORDER BY e.startDate ASC")
//    List<Event> findTodayEvents();

    // Find events for this week
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :startOfWeek AND :endOfWeek OR e.endDate BETWEEN :startOfWeek AND :endOfWeek ORDER BY e.startDate ASC")
    List<Event> findThisWeekEvents(@Param("startOfWeek") LocalDateTime startOfWeek,
                                   @Param("endOfWeek") LocalDateTime endOfWeek);

    // Find events for this month
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :startOfMonth AND :endOfMonth OR e.endDate BETWEEN :startOfMonth AND :endOfMonth ORDER BY e.startDate ASC")
    List<Event> findThisMonthEvents(@Param("startOfMonth") LocalDateTime startOfMonth,
                                    @Param("endOfMonth") LocalDateTime endOfMonth);

    // CORREÇÃO: Novo método para eventos que começam hoje ou no futuro
    @Query("SELECT e FROM Event e WHERE e.startDate >= :startOfDay ORDER BY e.startDate ASC")
    List<Event> findEventsFromToday(@Param("startOfDay") LocalDateTime startOfDay);
}
