package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.StudentSummaryDTO;
import com.schoolagenda.application.web.dto.request.ResponsibleStudentRequest;
import com.schoolagenda.application.web.dto.response.ResponsibleDashboardResponse;
import com.schoolagenda.application.web.dto.response.ResponsibleStudentResponse;
import com.schoolagenda.domain.model.ResponsibleStudent;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.MessageRepository;
import com.schoolagenda.domain.repository.ResponsibleStudentRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.ResponsibleStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponsibleStudentServiceImpl implements ResponsibleStudentService {

    private final ResponsibleStudentRepository responsibleStudentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public ResponsibleStudentServiceImpl(ResponsibleStudentRepository responsibleStudentRepository, UserRepository userRepository, StudentRepository studentRepository, MessageRepository messageRepository) {
        this.responsibleStudentRepository = responsibleStudentRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public ResponsibleStudentResponse createRelationship(ResponsibleStudentRequest request) {
        // Check if relationship already exists
        if (responsibleStudentRepository.existsByResponsibleIdAndStudentId(
                request.getResponsibleId(), request.getStudentId())) {
            throw new RuntimeException("Relationship already exists between responsible and student");
        }

        // Find responsible
        User responsible = userRepository.findById(request.getResponsibleId())
                .orElseThrow(() -> new RuntimeException("Responsible not found with id: " + request.getResponsibleId()));

        // Find student
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + request.getStudentId()));

        // Create and save relationship
        ResponsibleStudent relationship = new ResponsibleStudent(responsible, student);
        ResponsibleStudent savedRelationship = responsibleStudentRepository.save(relationship);

        return convertToResponse(savedRelationship);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsibleStudentResponse getRelationshipById(Long id) {
        ResponsibleStudent relationship = responsibleStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relationship not found with id: " + id));

        return convertToResponse(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsibleStudentResponse> getRelationshipsByResponsible(Long responsibleId) {
        List<ResponsibleStudent> relationships = responsibleStudentRepository.findStudentsByResponsibleId(responsibleId);

        return relationships.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsibleStudentResponse> getRelationshipsByStudent(Long studentId) {
        List<ResponsibleStudent> relationships = responsibleStudentRepository.findResponsiblesByStudentId(studentId);

        return relationships.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsibleStudentResponse getRelationship(Long responsibleId, Long studentId) {
        ResponsibleStudent relationship = responsibleStudentRepository
                .findByResponsibleIdAndStudentId(responsibleId, studentId)
                .orElseThrow(() -> new RuntimeException(
                        "Relationship not found for responsible: " + responsibleId + " and student: " + studentId));

        return convertToResponse(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipExists(Long responsibleId, Long studentId) {
        return responsibleStudentRepository.existsByResponsibleIdAndStudentId(responsibleId, studentId);
    }

    @Override
    @Transactional
    public void deleteRelationship(Long id) {
        if (!responsibleStudentRepository.existsById(id)) {
            throw new RuntimeException("Relationship not found with id: " + id);
        }
        responsibleStudentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteRelationship(Long responsibleId, Long studentId) {
        ResponsibleStudent relationship = responsibleStudentRepository
                .findByResponsibleIdAndStudentId(responsibleId, studentId)
                .orElseThrow(() -> new RuntimeException(
                        "Relationship not found for responsible: " + responsibleId + " and student: " + studentId));

        responsibleStudentRepository.delete(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public long getStudentCountByResponsible(Long responsibleId) {
        return responsibleStudentRepository.countByResponsibleId(responsibleId);
    }

    /**
     * Converts ResponsibleStudent entity to Response DTO
     */
    private ResponsibleStudentResponse convertToResponse(ResponsibleStudent relationship) {
        return new ResponsibleStudentResponse(
                relationship.getId(),
                relationship.getResponsible().getId(),
                relationship.getResponsible().getName(),
                relationship.getResponsible().getEmail(),
                relationship.getStudent().getId(),
//                relationship.getStudent().getName(),
                relationship.getStudent().getFullName(),
                relationship.getStudent().getClassName(), // Assuming Student entity has getClassName()
                relationship.getStudent().getGlobalAverage().setScale(2, RoundingMode.HALF_UP),
                relationship.getCreatedAt(),
                relationship.getUpdatedAt()
        );
    }
}