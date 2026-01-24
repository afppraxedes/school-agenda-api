package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.TimetableRequest;
import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<TimetableResponse> create(@Valid @RequestBody TimetableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<TimetableResponse> update(@PathVariable Long id, @Valid @RequestBody TimetableRequest request) {
        return ResponseEntity.ok(timetableService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timetableService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // LEITURA: Aberto a Professores, Alunos e Responsáveis
    @GetMapping("/school-class/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<List<TimetableResponse>> getByClass(@PathVariable Long id) {
        return ResponseEntity.ok(timetableService.findBySchoolClassId(id));
    }

    @GetMapping("/teacher/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<List<TimetableResponse>> getByTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(timetableService.findByTeacherId(id));
    }

    @GetMapping("/school-class/{id}/pdf")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = timetableService.generateTimetablePdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("horario_turma.pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/next")
    @PreAuthorize("isAuthenticated()") // Qualquer um logado pode ver sua própria "próxima aula"
    public ResponseEntity<TimetableResponse> getNext(@AuthenticationPrincipal AgendaUserDetails currentUser) {
        return ResponseEntity.ok(timetableService.getNextClass(currentUser));
    }

    @GetMapping("/current-or-next")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TimetableResponse> getCurrentOrNext(@AuthenticationPrincipal AgendaUserDetails currentUser) {
        // Este método chama a lógica que busca no banco se há aula agora ou a próxima
        return ResponseEntity.ok(timetableService.getCurrentOrNextClass(currentUser));
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<List<TimetableResponse>> getTodaySchedule(
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        // O Service já contém a lógica de buscar o DayOfWeek.now()
        List<TimetableResponse> schedule = timetableService.getTodayScheduleForStudent(currentUser.getId());

        return ResponseEntity.ok(schedule);
    }
}
