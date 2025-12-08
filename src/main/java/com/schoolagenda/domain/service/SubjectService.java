package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.SubjectRequest;
import com.schoolagenda.application.web.dto.response.SubjectResponse;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubjectService {

    SubjectResponse create(SubjectRequest request);
    SubjectResponse update(Long id, SubjectRequest request);
    void delete(Long id);

    SubjectResponse findById(Long id);
    List<SubjectResponse> findAll();
    List<SubjectResponse> findByTeacher(Long teacherId);
    List<SubjectResponse> findBySchoolYear(String schoolYear);
    List<SubjectResponse> findActive();

    SubjectResponse toggleStatus(Long id);
    
}
