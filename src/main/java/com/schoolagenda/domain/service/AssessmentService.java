package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.assessment.AssessmentFilterRequest;
import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.domain.exception.InvalidFilterException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.specification.AssessmentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface AssessmentService {

    AssessmentResponse create(AssessmentRequest request);
    AssessmentResponse update(Long id, AssessmentRequest request);
    void delete(Long id);

    AssessmentResponse findById(Long id);
    List<AssessmentResponse> findAll();
    List<AssessmentResponse> findBySubject(Long subjectId);
    List<AssessmentResponse> findPublishedBySubject(Long subjectId);
    List<AssessmentResponse> findPublishedByFilters(Long subjectId, LocalDate startDate, LocalDate endDate);
    List<AssessmentResponse> findUpcoming(Integer days);

    AssessmentResponse publish(Long id);
    AssessmentResponse unpublish(Long id);

    PaginationResponse<AssessmentResponse> search(PaginationRequest pageRequest,
                                                         AssessmentFilterRequest filter);
    PaginationResponse<AssessmentResponse> findPublished(PaginationRequest pageRequest);
    PaginationResponse<AssessmentResponse> findBySubject(PaginationRequest pageRequest,
                                                                Long subjectId);
}
