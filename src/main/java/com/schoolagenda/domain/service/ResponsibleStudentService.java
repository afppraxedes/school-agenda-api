// src/main/java/com/schoolagenda/domain/service/ResponsibleStudentService.java
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.ResponsibleStudentRequest;
import com.schoolagenda.application.web.dto.response.ResponsibleDashboardResponse;
import com.schoolagenda.application.web.dto.response.ResponsibleStudentResponse;

import java.util.List;

public interface ResponsibleStudentService {

    // Create a new responsible-student relationship
    ResponsibleStudentResponse createRelationship(ResponsibleStudentRequest request);

    // Get relationship by ID
    ResponsibleStudentResponse getRelationshipById(Long id);

    // Get all relationships for a responsible
    List<ResponsibleStudentResponse> getRelationshipsByResponsible(Long responsibleId);

    // Get all relationships for a student
    List<ResponsibleStudentResponse> getRelationshipsByStudent(Long studentId);

    // Get specific relationship
    ResponsibleStudentResponse getRelationship(Long responsibleId, Long studentId);

    // Check if relationship exists
    boolean relationshipExists(Long responsibleId, Long studentId);

    // Delete relationship by ID
    void deleteRelationship(Long id);

    // Delete specific relationship
    void deleteRelationship(Long responsibleId, Long studentId);

    // Get student count for responsible
    long getStudentCountByResponsible(Long responsibleId);
}