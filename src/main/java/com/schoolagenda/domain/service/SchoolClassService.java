package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.SchoolClassRequest;
import com.schoolagenda.application.web.dto.response.SchoolClassResponse;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.SchoolClass;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SchoolClassService {

    SchoolClassResponse create(SchoolClassRequest request);
    SchoolClassResponse findById(Long id);
    List<SchoolClassResponse> findAll();
    SchoolClassResponse update(Long id, SchoolClassRequest request);
    void delete(Long id);

}
