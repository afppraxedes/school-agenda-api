package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.EventRequest;
import com.schoolagenda.application.web.dto.response.EventResponse;
import com.schoolagenda.domain.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id, @Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> responses = eventService.getAllEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        EventResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/range")
    public ResponseEntity<List<EventResponse>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EventResponse> responses = eventService.getEventsByDateRange(start, end);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> responses = eventService.getUpcomingEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<EventResponse>> getOngoingEvents() {
        List<EventResponse> responses = eventService.getOngoingEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/past")
    public ResponseEntity<List<EventResponse>> getPastEvents() {
        List<EventResponse> responses = eventService.getPastEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/today")
    public ResponseEntity<List<EventResponse>> getTodayEvents() {
        List<EventResponse> responses = eventService.getTodayEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/week")
    public ResponseEntity<List<EventResponse>> getThisWeekEvents() {
        List<EventResponse> responses = eventService.getThisWeekEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/month")
    public ResponseEntity<List<EventResponse>> getThisMonthEvents() {
        List<EventResponse> responses = eventService.getThisMonthEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<List<EventResponse>> getEventsByYearAndMonth(
            @PathVariable int year, @PathVariable int month) {
        List<EventResponse> responses = eventService.getEventsByYearAndMonth(year, month);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEventsByTitle(
            @RequestParam String title) {
        List<EventResponse> responses = eventService.searchEventsByTitle(title);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/from-today")
    public ResponseEntity<List<EventResponse>> getEventsFromToday() {
        List<EventResponse> responses = eventService.getEventsFromToday();
        return ResponseEntity.ok(responses);
    }
}
