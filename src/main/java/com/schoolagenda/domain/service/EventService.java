package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.EventRequest;
import com.schoolagenda.application.web.dto.response.EventResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    // Create a new event
    EventResponse createEvent(EventRequest request);

    // Get event by ID
    EventResponse getEventById(Long id);

    // Get all events
    List<EventResponse> getAllEvents();

    // Get events by date range
    List<EventResponse> getEventsByDateRange(LocalDateTime start, LocalDateTime end);

    // Get upcoming events
    List<EventResponse> getUpcomingEvents();

    // Get ongoing events
    List<EventResponse> getOngoingEvents();

    // Get past events
    List<EventResponse> getPastEvents();

    // Get events for today
    List<EventResponse> getTodayEvents();

    // Get events for this week
    List<EventResponse> getThisWeekEvents();

    // Get events for this month
    List<EventResponse> getThisMonthEvents();

    // Get events by year and month
    List<EventResponse> getEventsByYearAndMonth(int year, int month);

    // Search events by title
    List<EventResponse> searchEventsByTitle(String title);

    public List<EventResponse> getEventsFromToday();

    // Update event
    EventResponse updateEvent(Long id, EventRequest request);

    // Delete event
    void deleteEvent(Long id);

    // Validate event dates
    boolean validateEventDates(LocalDateTime startDate, LocalDateTime endDate);
}
