package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.application.web.mapper.GradeMapper;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Grade;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.AssessmentRepository;
import com.schoolagenda.domain.repository.GradeRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;
    private final GradeMapper gradeMapper;

    @Override
    @Transactional
    public GradeResponse create(GradeRequest request) {
        log.info("Creating a new grade for assessment ID: {}, student ID: {}",
                request.getAssessmentId(), request.getStudentUserId());

        validateGradeRequest(request);

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment not found by ID: " + request.getAssessmentId()));

        User student = userRepository.findById(request.getStudentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found by ID: " + request.getStudentUserId()));

        User gradedBy = null;
        if (request.getGradedByUserId() != null) {
            gradedBy = userRepository.findById(request.getGradedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found by ID: " + request.getGradedByUserId()));
        }

        // Verifica se já existe nota para esta combinação
        if (gradeRepository.existsByAssessmentIdAndStudentId(
                request.getAssessmentId(), request.getStudentUserId())) {
            throw new IllegalArgumentException(
                    "A grade already exists for this student in this assessment");
        }

        Grade grade = gradeMapper.toEntity(request);
        grade.setAssessment(assessment);
        grade.setStudent(student);
        grade.setGradedBy(gradedBy);
        grade.setGradedAt(LocalDateTime.now());

        // Se ausente ou justificada, nota deve ser null
        if (Boolean.TRUE.equals(request.getAbsent()) || Boolean.TRUE.equals(request.getExcused())) {
            grade.setScore(null);
        }

        Grade savedGrade = gradeRepository.save(grade);
        log.info("Grade created with ID: {}", savedGrade.getId());

        return gradeMapper.toResponse(savedGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse findById(Long id) {
        log.debug("Searching for grade with ID: {}", id);

        Grade grade = gradeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grade not found with ID: " + id));

        return gradeMapper.toResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse findByAssessmentAndStudent(Long assessmentId, Long studentId) {
        log.debug("Searching for grade for assessment {} and student {}", assessmentId, studentId);

        Grade grade = gradeRepository.findByAssessmentIdAndStudentId(assessmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found for this combination."));

        return gradeMapper.toResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findByAssessment(Long assessmentId) {
        log.debug("Searching for grades for assessment ID: {}", assessmentId);
        return gradeRepository.findByAssessmentId(assessmentId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findByStudent(Long studentId) {
        log.debug("Searching for grades for student ID: {}", studentId);

        List<Grade> response = gradeRepository.findByStudentId(studentId);

        if(response.isEmpty()) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        return response.stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findByStudentAndSubject(Long studentId, Long subjectId) {
        log.debug("Searching for student grades {} in the subject {}", studentId, subjectId);
        return gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findUngradedByAssessment(Long assessmentId) {
        log.debug("Searching for students without a grade in assessment ID: {}", assessmentId);
        return gradeRepository.findUngradedByAssessmentId(assessmentId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GradeResponse update(Long id, GradeRequest request) {
        log.info("Updating grade ID: {}", id);

        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grade not found with ID: " + id));

        validateGradeRequest(request, id);

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment not found with ID: " + request.getAssessmentId()));

        User student = userRepository.findById(request.getStudentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + request.getStudentUserId()));

        User gradedBy = null;
        if (request.getGradedByUserId() != null) {
            gradedBy = userRepository.findById(request.getGradedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with ID: " + request.getGradedByUserId()));
        }

        gradeMapper.updateEntity(request, grade);
        grade.setAssessment(assessment);
        grade.setStudent(student);
        grade.setGradedBy(gradedBy);
        grade.setGradedAt(LocalDateTime.now());

        // Se ausente ou justificada, nota deve ser null
        if (Boolean.TRUE.equals(request.getAbsent()) || Boolean.TRUE.equals(request.getExcused())) {
            grade.setScore(null);
        }

        Grade updatedGrade = gradeRepository.save(grade);
        log.info("Grade updated with ID: {}", id);

        return gradeMapper.toResponse(updatedGrade);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting grade ID: {}", id);

        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note not found with ID: " + id);
        }

        gradeRepository.deleteById(id);
        log.info("Grade deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public GradeResponse bulkCreate(List<GradeRequest> requests) {
        log.info("Creating {} grades in batch", requests.size());

        List<Grade> grades = requests.stream()
                .map(request -> {
                    Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Assessment not found with ID: " + request.getAssessmentId()));

                    User student = userRepository.findById(request.getStudentUserId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Student not found with ID: " + request.getStudentUserId()));

                    User gradedBy = null;
                    if (request.getGradedByUserId() != null) {
                        gradedBy = userRepository.findById(request.getGradedByUserId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "User not found with ID: " + request.getGradedByUserId()));
                    }

                    Grade grade = gradeMapper.toEntity(request);
                    grade.setAssessment(assessment);
                    grade.setStudent(student);
                    grade.setGradedBy(gradedBy);
                    grade.setGradedAt(LocalDateTime.now());

                    return grade;
                })
                .toList();

        List<Grade> savedGrades = gradeRepository.saveAll(grades);
        log.info("{} Grades created in batch", savedGrades.size());

        // Retorna a primeira nota criada (ou poderia retornar todas)
        return savedGrades.isEmpty() ? null : gradeMapper.toResponse(savedGrades.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateStudentAverage(Long studentId, Long subjectId) {
        log.debug("Calculating student average {} in the subject {}", studentId, subjectId);

        // TODO: fazer a validação para "studentId" e "subjectId" não encontrado.
        // Para buscas de "grade", "student", "assessment" e "subject" criar métodos internos
        // para verificar a existência.

        return gradeRepository.calculateAverageByStudentAndSubject(studentId, subjectId);
    }

    private void validateGradeRequest(GradeRequest request) {
        validateGradeRequest(request, null);
    }

    private void validateGradeRequest(GradeRequest request, Long excludeId) {
        // Valida se a nota não excede o máximo da avaliação
        // Validação específica para BigDecimal
        if (request.getScore() != null) {
            Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assessment not found with ID: " + request.getAssessmentId()));

            if (assessment.getMaxScore() != null &&
                    request.getScore().compareTo(assessment.getMaxScore()) > 0) {
                throw new IllegalArgumentException(
                        String.format("A grade (%s) cannot be higher than the maximum grade for the assessment. (%s)",
                                request.getScore(), assessment.getMaxScore()));
            }
        }
//        if (request.getScore() != null) {
//            Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Avaliação não encontrada com ID: " + request.getAssessmentId()));
//
//            if (assessment.getMaxScore() != null && request.getScore() > assessment.getMaxScore()) {
//                throw new IllegalArgumentException(
//                        String.format("A nota (%.2f) não pode ser maior que a nota máxima da avaliação (%.2f)",
//                                request.getScore(), assessment.getMaxScore()));
//            }
//        }
    }
}
