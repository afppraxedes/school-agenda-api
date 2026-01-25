package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.EventRequest;
import com.schoolagenda.application.web.dto.response.EventResponse;
import com.schoolagenda.application.web.dto.response.SchoolEventResponse;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Event;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.EventRepository;
import com.schoolagenda.domain.repository.SchoolEventRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final SchoolEventRepository schoolEventRepository;

    public EventServiceImpl(EventRepository eventRepository, StudentRepository studentRepository, SchoolEventRepository schoolEventRepository) {
        this.eventRepository = eventRepository;
        this.studentRepository = studentRepository;
        this.schoolEventRepository = schoolEventRepository;
    }

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        // Validate dates
        if (!validateEventDates(request.getStartDate(), request.getEndDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        Event event = new Event(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getColor()
        );

        Event savedEvent = eventRepository.save(event);
        return convertToResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        return convertToResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Event> events = eventRepository.findByStartDateBetweenOrderByStartDateAsc(start, end);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<EventResponse> getUpcomingEvents() {
//        List<Event> events = eventRepository.findByStartDateAfterOrderByStartDateAsc(LocalDateTime.now());
//
//        return events.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        // CORREÇÃO: Usa o método específico para eventos futuros
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getOngoingEvents() {
        List<Event> events = eventRepository.findOngoingEvents(LocalDateTime.now());

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getPastEvents() {
        List<Event> events = eventRepository.findByEndDateBeforeOrderByStartDateDesc(LocalDateTime.now());

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<EventResponse> getTodayEvents() {
//        List<Event> events = eventRepository.findTodayEvents();
//
//        return events.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getTodayEvents() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Event> events = eventRepository.findTodayEvents(startOfDay, endOfDay);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getThisWeekEvents() {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59);

        List<Event> events = eventRepository.findThisWeekEvents(startOfWeek, endOfWeek);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getThisMonthEvents() {
        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

        List<Event> events = eventRepository.findThisMonthEvents(startOfMonth, endOfMonth);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByYearAndMonth(int year, int month) {
        List<Event> events = eventRepository.findByYearAndMonth(year, month);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> searchEventsByTitle(String title) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCaseOrderByStartDateAsc(title);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Método adicional se precisar de eventos a partir de hoje (incluindo os que começam hoje)
    // TODO: Implementar esse método no "controller"!
    public List<EventResponse> getEventsFromToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<Event> events = eventRepository.findEventsFromToday(startOfDay);

        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        // Validate dates
        if (!validateEventDates(request.getStartDate(), request.getEndDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setColor(request.getColor());

        Event updatedEvent = eventRepository.save(event);
        return convertToResponse(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public boolean validateEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        return endDate.isAfter(startDate);
    }

    /**
     * Converts Event entity to Response DTO
     */
    private EventResponse convertToResponse(Event event) {
        String status;
        if (event.isOngoing()) {
            status = "ONGOING";
        } else if (event.isUpcoming()) {
            status = "UPCOMING";
        } else {
            status = "PAST";
        }

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getColor(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                status
        );
    }

    public List<SchoolEventResponse> findUpcomingByStudent(Long studentId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        // Busca os próximos 5 eventos da turma ou da escola (feriados)
        return schoolEventRepository.findUpcomingEvents(student.getSchoolClass().getId(), PageRequest.of(0, 5))
                .stream()
                .map(e -> new SchoolEventResponse(
                        e.getTitle(),
                        e.getStartDate(),
                        e.getType().toString(),
                        e.getLocation()
                ))
                .toList();
    }
}
