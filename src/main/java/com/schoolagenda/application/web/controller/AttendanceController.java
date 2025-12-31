package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.AttendanceBulkRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.application.web.dto.response.AttendanceStatsResponse;
import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.AttendanceService;
import com.schoolagenda.domain.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor // Injeção via construtor garantida para campos final
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final TimetableService timetableService;

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<Void> saveBulk(
            @Valid @RequestBody AttendanceBulkRequest request,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        // Chamando o nome padronizado
        attendanceService.saveBulk(request, currentUser.getId());
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

    @GetMapping("/suggested-class")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<TimetableResponse> getSuggestedClass(@AuthenticationPrincipal AgendaUserDetails user) {
        // Usa a lógica que criamos no Timetable para pegar a aula de agora
        return ResponseEntity.ok(timetableService.getCurrentOrNextClass(user));
    }

    @GetMapping("/student/{studentId}/history")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<List<AttendanceResponse>> getHistory(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(attendanceService.getStudentHistory(studentId, start, end));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<AttendanceStatsResponse> getStats(
            @RequestParam Long studentId,
            @RequestParam Long subjectId) {

        long totalAbsences = attendanceService.getTotalAbsences(studentId, subjectId);
        long totalClasses = attendanceService.getTotalClasses(studentId, subjectId);

        return ResponseEntity.ok(new AttendanceStatsResponse(totalAbsences, totalClasses));
    }
}