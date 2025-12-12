package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.application.web.dto.common.grade.GradeStatistics;
import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.application.web.mapper.GradeMapper;
import com.schoolagenda.domain.exception.InvalidFilterException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Grade;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.AssessmentRepository;
import com.schoolagenda.domain.repository.GradeRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.GradeService;
import com.schoolagenda.domain.specification.GradeSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    // ========== MÉTODOS PAGINADOS ==========
    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> search(PaginationRequest pageRequest,
                                                    GradeFilterRequest filter) {
        log.debug("Buscando notas paginadas: {}", filter);

        validateFilter(filter);
        Specification<Grade> spec = buildSpecification(filter);

        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
        logSearchMetrics(page, filter);

        return PaginationResponse.of(page.map(gradeMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> findByAssessment(PaginationRequest pageRequest,
                                                        Long assessmentId) {
        Specification<Grade> spec = GradeSpecifications.byAssessment(assessmentId);
        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(gradeMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> findByStudent(PaginationRequest pageRequest,
                                                     Long studentId) {
        Specification<Grade> spec = GradeSpecifications.byStudent(studentId)
                .and(GradeSpecifications.isGraded()); // Apenas notas lançadas

        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(gradeMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> findUngradedByAssessment(PaginationRequest pageRequest,
                                                                Long assessmentId) {
        Specification<Grade> spec = GradeSpecifications.byAssessment(assessmentId)
                .and(GradeSpecifications.isNotGraded());

        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(gradeMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> findPassingGrades(PaginationRequest pageRequest,
                                                         BigDecimal passingScore) {
        Specification<Grade> spec = GradeSpecifications.isGraded()
                .and(GradeSpecifications.isPassing(passingScore));

        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(gradeMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> findByStudentAndSubject(PaginationRequest pageRequest,
                                                               Long studentId,
                                                               Long subjectId) {
        Specification<Grade> spec = GradeSpecifications.byStudent(studentId)
                .and(GradeSpecifications.bySubject(subjectId))
                .and(GradeSpecifications.isGraded());

        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(gradeMapper::toResponse));
    }

    // CALCULAR ESTATÍSTICA
//    @Transactional(readOnly = true)
//    public GradeStatistics calculateStatistics(Long assessmentId) {
//        Specification<Grade> spec = GradeSpecifications.byAssessment(assessmentId)
//                .and(GradeSpecifications.isGraded());
//
//        List<Grade> grades = gradeRepository.findAll(spec);
//
//        // Calcula média, maior, menor, etc.
//        return GradeStatistics.from(grades);
//    }

    // ========== MÉTODOS LEGACY (se necessário) ==========

//    @Transactional(readOnly = true)
//    public List<GradeResponse> findAll() {
//        return gradeRepository.findAll().stream()
//                .map(gradeMapper::toResponse)
//                .toList();
//    }

    // ========== MÉTODOS PRIVADOS AUXILIARES ==========

    private void validateFilter(GradeFilterRequest filter) {
        if (filter == null) return;

        // Valida intervalo de notas
        if (filter.hasScoreRange() && !filter.isScoreRangeValid()) {
            throw new InvalidFilterException("Nota mínima não pode ser maior que nota máxima");
        }

        // Valida notas negativas
        if (filter.getMinScore() != null && filter.getMinScore().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFilterException("Nota não pode ser negativa");
        }

        if (filter.getMaxScore() != null && filter.getMaxScore().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFilterException("Nota não pode ser negativa");
        }
    }

    private Specification<Grade> buildSpecification(GradeFilterRequest filter) {
        if (filter == null) {
            return Specification.allOf();
        }
        return GradeSpecifications.withFilters(filter);
    }

    private void logSearchMetrics(Page<Grade> page, GradeFilterRequest filter) {
        if (log.isDebugEnabled()) {
            log.debug("Busca de notas concluída - Resultados: {}/{} | Filtro: {}",
                    page.getNumberOfElements(),
                    page.getTotalElements(),
                    filter != null ? filter.toString() : "Nenhum");
        }

        // Estatísticas úteis
        if (page.getTotalElements() > 0 && filter != null && filter.getMinScore() != null) {
            long passingCount = page.getContent().stream()
                    .filter(grade -> grade.getScore() != null
                            && grade.getScore().compareTo(filter.getMinScore()) >= 0)
                    .count();

            log.debug("Notas acima de {}: {}/{}",
                    filter.getMinScore(), passingCount, page.getNumberOfElements());
        }
    }
}
