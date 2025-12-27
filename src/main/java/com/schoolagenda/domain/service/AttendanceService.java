package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.AttendanceBulkRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceService {

    void saveAll(AttendanceBulkRequest request, AgendaUserDetails currentUser);
    
    Optional<AttendanceResponse> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);
    long countByStudentIdAndSubjectIdAndPresentFalse(Long studentId, Long subjectId);
    long countByStudentIdAndSubjectId(Long studentId, Long subjectId);

}
