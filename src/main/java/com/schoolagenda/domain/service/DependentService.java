package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.GradeStudentDTO;

import java.util.List;

public interface DependentService {
    List<GradeStudentDTO> getDependentsPerformance(Long responsibleId);
}
