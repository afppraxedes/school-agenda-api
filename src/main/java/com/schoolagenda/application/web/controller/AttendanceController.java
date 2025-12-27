package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.AttendanceBulkRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor // Injeção via construtor garantida para campos final
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR')")
    public ResponseEntity<Void> registerAttendance(
            @Valid @RequestBody AttendanceBulkRequest request,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        attendanceService.saveAll(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<AttendanceResponse> findByDate(
            @RequestParam Long studentId,
            @RequestParam Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return attendanceService.findByStudentIdAndSubjectIdAndDate(studentId, subjectId, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count-absences")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<Long> countAbsences(@RequestParam Long studentId, @RequestParam Long subjectId) {
        return ResponseEntity.ok(attendanceService.countByStudentIdAndSubjectIdAndPresentFalse(studentId, subjectId));
    }

    @GetMapping("/total-classes")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<Long> totalClasses(@RequestParam Long studentId, @RequestParam Long subjectId) {
        return ResponseEntity.ok(attendanceService.countByStudentIdAndSubjectId(studentId, subjectId));
    }
}