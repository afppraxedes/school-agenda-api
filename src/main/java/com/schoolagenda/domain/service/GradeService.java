package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.application.web.dto.response.ReportCardResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;

import java.math.BigDecimal;
import java.util.List;

public interface GradeService {

//    GradeResponse create(GradeRequest request);
//    GradeResponse update(Long id, GradeRequest request);
    GradeResponse create(GradeRequest request, AgendaUserDetails currentUser);
    GradeResponse update(Long id, GradeRequest request, AgendaUserDetails currentUser);
    public void delete(Long id, AgendaUserDetails currentUser);
//    void delete(Long id);
    GradeResponse bulkCreate(List<GradeRequest> requests, AgendaUserDetails currentUser);
//    GradeResponse bulkCreate(List<GradeRequest> requests);
    
    GradeResponse findById(Long id);
    GradeResponse findByAssessmentAndStudent(Long assessmentId, Long studentId);
    List<GradeResponse> findByAssessment(Long assessmentId);
    List<GradeResponse> findByStudent(Long studentId);
    List<GradeResponse> findByStudentAndSubject(Long studentId, Long subjectId);
    List<GradeResponse> findUngradedByAssessment(Long assessmentId);

    Double calculateStudentAverage(Long studentId, Long subjectId);
//    ReportCardResponse getStudentReportCard(Long studentUserId);

    ReportCardResponse getStudentReportCard(Long studentUserId, AgendaUserDetails currentUser);

    // ========== MÉTODOS PAGINADOS ==========
    // TODO: Método anterior antes do "RBAC"
//    PaginationResponse<GradeResponse> search(PaginationRequest pageRequest,
//                                                    GradeFilterRequest filter);

    PaginationResponse<GradeResponse> searchGrades(
            PaginationRequest pageRequest,
            GradeFilterRequest filter,
            AgendaUserDetails currentUser);

    PaginationResponse<GradeResponse> findByAssessment(PaginationRequest pageRequest,
                                                              Long assessmentId);
    PaginationResponse<GradeResponse> findByStudent(PaginationRequest pageRequest,
                                                           Long studentId);
    PaginationResponse<GradeResponse> findUngradedByAssessment(PaginationRequest pageRequest,
                                                                      Long assessmentId);
    PaginationResponse<GradeResponse> findPassingGrades(PaginationRequest pageRequest,
                                                               BigDecimal passingScore);
    PaginationResponse<GradeResponse> findByStudentAndSubject(PaginationRequest pageRequest,
                                                                     Long studentId,
                                                                     Long subjectId);
    
}
