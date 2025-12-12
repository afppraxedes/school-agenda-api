package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Grade;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.specification.GradeSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface GradeService {

    GradeResponse create(GradeRequest request);
    GradeResponse update(Long id, GradeRequest request);
    void delete(Long id);
    
    GradeResponse findById(Long id);
    GradeResponse findByAssessmentAndStudent(Long assessmentId, Long studentId);
    List<GradeResponse> findByAssessment(Long assessmentId);
    List<GradeResponse> findByStudent(Long studentId);
    List<GradeResponse> findByStudentAndSubject(Long studentId, Long subjectId);
    List<GradeResponse> findUngradedByAssessment(Long assessmentId);
    
    GradeResponse bulkCreate(List<GradeRequest> requests);
    Double calculateStudentAverage(Long studentId, Long subjectId);

    // ========== MÉTODOS PAGINADOS ==========
    PaginationResponse<GradeResponse> search(PaginationRequest pageRequest,
                                                    GradeFilterRequest filter);
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
