// src/main/java/com/schoolagenda/application/web/controller/TeacherClassController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.GradeStudentDTO;
import com.schoolagenda.application.web.dto.request.SaveGradeRequest;
import com.schoolagenda.application.web.dto.request.SingleStudentGradesRequest;
import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.PerformanceHistoryResponse;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.service.GradeService;
import com.schoolagenda.domain.service.ReportCardService;
import com.schoolagenda.domain.service.TeacherClassService;
import com.schoolagenda.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher-classes")
@RequiredArgsConstructor
public class TeacherClassController {

    private final TeacherClassService teacherClassService;
    private final UserService userService;
    private final ReportCardService reportCardService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<TeacherClassResponse> createTeacherClass(
            @Valid @RequestBody TeacherClassRequest request) {
        TeacherClassResponse response = teacherClassService.createTeacherClass(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<TeacherClassResponse> updateTeacherClass(
            @PathVariable Long id, @Valid @RequestBody TeacherClassRequest request) {
        TeacherClassResponse response = teacherClassService.updateTeacherClass(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<Void> deleteTeacherClass(@PathVariable Long id) {
        teacherClassService.deleteTeacherClass(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-params")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<Void> deleteTeacherClassByParams(
            @RequestParam Long teacherId,
            @RequestParam Long subjectId,
            @RequestParam Long schoolClassId) {

        teacherClassService.deleteTeacherClass(teacherId, subjectId, schoolClassId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<TeacherClassResponse> getTeacherClassById(@PathVariable Long id) {
        TeacherClassResponse response = teacherClassService.getTeacherClassById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<List<TeacherClassResponse>> getClassesByTeacher(
            @PathVariable Long teacherId) {
        List<TeacherClassResponse> responses = teacherClassService.getClassesByTeacher(teacherId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/class/{schoolClassId}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<List<TeacherClassResponse>> getTeachersByClass(
            @PathVariable Long schoolClassId) {
        List<TeacherClassResponse> responses = teacherClassService.getTeachersByClass(schoolClassId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/teacher/{teacherId}/subject/{subjectId}/schoolClass/{schoolClassId}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<TeacherClassResponse> getTeacherClass(
            @PathVariable Long teacherId, @PathVariable Long subjectId, @PathVariable Long schoolClassId) {
        TeacherClassResponse response = teacherClassService.getTeacherClass(teacherId, subjectId, schoolClassId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}/subject/{subjectId}/schoolClass/{schoolClassId}/exists")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<Boolean> teacherClassExists(
            @PathVariable Long teacherId, @PathVariable Long subjectId, @PathVariable Long schoolClassId) {
        boolean exists = teacherClassService.teacherClassExists(teacherId, subjectId, schoolClassId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/teacher/{teacherId}/class-count")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<Long> getClassCountByTeacher(@PathVariable Long teacherId) {
        long count = teacherClassService.getClassCountByTeacher(teacherId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/classes/distinct")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<List<Long>> getAllDistinctSchoolClassIds() {
        List<Long> classNames = teacherClassService.getAllDistinctSchoolClassIds();
        return ResponseEntity.ok(classNames);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping(value = "/subjects")
    public ResponseEntity<List<Long>> findSubjectIdsByTeacherId(@RequestParam Long teacherId) {
        List<Long> techIds = teacherClassService.findSubjectIdsByTeacherId(teacherId);
        return ResponseEntity.ok(techIds);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping(value = "/students")
    public ResponseEntity<List<Long>> findStudentIdsByResponsibleId(@RequestParam Long responsibleId) {
        List<Long> responsibleIds = teacherClassService.findStudentIdsByResponsibleId(responsibleId);;
        return ResponseEntity.ok(responsibleIds);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getTeachers() {
        return ResponseEntity.ok(userService.findAllByProfile(UserRole.TEACHER.name()));
    }

//    @GetMapping("/{classId}/students-grades")
//    @PreAuthorize("hasAuthority('TEACHER')")
//    public ResponseEntity<List<StudentGradeResponse>> getStudentsGrades(@PathVariable Long classId) {
//        // Busca os alunos da turma informada e mapeia para o DTO com as notas atuais
//        List<StudentGradeResponse> grades = teacherClassService.getGradesByClass(classId);
//        return ResponseEntity.ok(grades);
//    }

    // TODO: OLHAR DEPOIS NO "GEMINI", POIS ESTE MÉTODO ESTÁ DANDO ERRO, MAS ESTAVA NAS IMPLEMENTAÇÕES SUGERIDAS!
//    @PostMapping("/save-grades")
//    @PreAuthorize("hasAuthority('TEACHER')")
//    public ResponseEntity<Void> saveGrades(@RequestBody List<SaveGradeRequest> grades) {
////        teacherClassService.updateGrades(grades);
//        teacherClassService.updateStudentGrades(grades);
//        return ResponseEntity.ok().build();
//    }

    // NOVA IMPLEMENTAÇÃO
    @PostMapping("/save-grades")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> saveGrades(@RequestBody List<SaveGradeRequest> grades) {
        // RESOLVIDO: Agora o service possui o método que aceita List
        teacherClassService.saveAllGrades(grades);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students/{studentId}/average")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'STUDENT', 'ADMINISTRATOR')")
    public ResponseEntity<Double> getStudentAverage(@PathVariable Long studentId) {
        Double average = teacherClassService.calculateStudentGlobalAverage(studentId);
        return ResponseEntity.ok(average);
    }

    // 1.3 Endpoint para buscar notas da turma
    @GetMapping("/{id}/students-grades")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<List<GradeStudentDTO>> getStudentsGrades(@PathVariable Long id) {
        return ResponseEntity.ok(teacherClassService.getStudentsGrades(id));
    }

    // 3.3 Endpoint para envio manual de push (Utilidade Admin/Teacher)
    @PostMapping("/notify-user/{userId}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<Void> notifyUser(@PathVariable Long userId, @RequestParam String title, @RequestParam String msg) {
        // pushService.sendPushToUser(userId, title, msg);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/save-student-grades")
//    @PreAuthorize("hasAuthority('TEACHER')")
//    public ResponseEntity<Void> updateStudentGrades(@RequestBody SingleStudentGradesRequest request) {
//        teacherClassService.updateStudentGrades(request);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/save-student-grades")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> updateStudentGrades(@RequestBody List<SingleStudentGradesRequest> request) {
        // Chamamos o novo método de processamento em lote
        teacherClassService.saveAllStudentGrades(request);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/student/{studentId}/history")
//    @PreAuthorize("hasAnyAuthority('RESPONSIBLE', 'STUDENT')")
//    public ResponseEntity<PerformanceHistoryResponse> getPerformanceHistory(@PathVariable Long studentId) {
//        return ResponseEntity.ok(teacherClassService.getStudentHistory(studentId));
//    }

    @GetMapping("/{studentId}/performance-history")
    public ResponseEntity<PerformanceHistoryResponse> getStudentHistory(
            @PathVariable Long studentId,
            @RequestParam Long teacherClassId) { // Parâmetro adicionado para o filtro por disciplina

        PerformanceHistoryResponse history = teacherClassService.getStudentHistory(studentId, teacherClassId);
        return ResponseEntity.ok(history);
    }

//    @GetMapping("/student/{studentId}/report-card")
//    @PreAuthorize("hasAnyAuthority('RESPONSIBLE', 'STUDENT', 'TEACHER')")
//    public ResponseEntity<byte[]> downloadReportCard(@PathVariable Long studentId) {
//        byte[] pdf = teacherClassService.generateReportCardPDF(studentId);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("attachment", "boletim.pdf");
//
//        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
//    }

    @GetMapping("/student/{studentId}/report-card")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'RESPONSIBLE', 'STUDENT')")
    public ResponseEntity<byte[]> getReportCard(@PathVariable Long studentId) {
        byte[] pdf = reportCardService.generateReportCard(studentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boletim_" + studentId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}