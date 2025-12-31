package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.AttendanceBulkRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

//    void saveAll(AttendanceBulkRequest request, AgendaUserDetails currentUser);
    // Lançamento em lote
    void saveBulk(AttendanceBulkRequest request, Long teacherUserId);

    Optional<AttendanceResponse> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);
    long countByStudentIdAndSubjectIdAndPresentFalse(Long studentId, Long subjectId);
    long countByStudentIdAndSubjectId(Long studentId, Long subjectId);

    // Consultas utilitárias
    List<AttendanceResponse> getStudentHistory(Long studentId, LocalDate start, LocalDate end);
    long getTotalAbsences(Long studentId, Long subjectId);
    long getTotalClasses(Long studentId, Long subjectId);

}
