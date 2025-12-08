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
        log.info("Criando nova nota para avaliação ID: {}, estudante ID: {}",
                request.getAssessmentId(), request.getStudentUserId());

        validateGradeRequest(request);

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avaliação não encontrada com ID: " + request.getAssessmentId()));

        User student = userRepository.findById(request.getStudentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + request.getStudentUserId()));

        User gradedBy = null;
        if (request.getGradedByUserId() != null) {
            gradedBy = userRepository.findById(request.getGradedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuário não encontrado com ID: " + request.getGradedByUserId()));
        }

        // Verifica se já existe nota para esta combinação
        if (gradeRepository.existsByAssessmentIdAndStudentId(
                request.getAssessmentId(), request.getStudentUserId())) {
            throw new IllegalArgumentException(
                    "Já existe uma nota para este estudante nesta avaliação");
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
        log.info("Nota criada com ID: {}", savedGrade.getId());

        return gradeMapper.toResponse(savedGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse findById(Long id) {
        log.debug("Buscando nota com ID: {}", id);

        Grade grade = gradeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nota não encontrada com ID: " + id));

        return gradeMapper.toResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse findByAssessmentAndStudent(Long assessmentId, Long studentId) {
        log.debug("Buscando nota para avaliação {} e estudante {}", assessmentId, studentId);

        Grade grade = gradeRepository.findByAssessmentIdAndStudentId(assessmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nota não encontrada para esta combinação"));

        return gradeMapper.toResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findByAssessment(Long assessmentId) {
        log.debug("Buscando notas da avaliação ID: {}", assessmentId);
        return gradeRepository.findByAssessmentId(assessmentId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findByStudent(Long studentId) {
        log.debug("Buscando notas do estudante ID: {}", studentId);
        return gradeRepository.findByStudentId(studentId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findByStudentAndSubject(Long studentId, Long subjectId) {
        log.debug("Buscando notas do estudante {} na disciplina {}", studentId, subjectId);
        return gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> findUngradedByAssessment(Long assessmentId) {
        log.debug("Buscando estudantes sem nota na avaliação ID: {}", assessmentId);
        return gradeRepository.findUngradedByAssessmentId(assessmentId).stream()
                .map(gradeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GradeResponse update(Long id, GradeRequest request) {
        log.info("Atualizando nota ID: {}", id);

        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nota não encontrada com ID: " + id));

        validateGradeRequest(request, id);

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avaliação não encontrada com ID: " + request.getAssessmentId()));

        User student = userRepository.findById(request.getStudentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + request.getStudentUserId()));

        User gradedBy = null;
        if (request.getGradedByUserId() != null) {
            gradedBy = userRepository.findById(request.getGradedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuário não encontrado com ID: " + request.getGradedByUserId()));
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
        log.info("Nota atualizada com ID: {}", id);

        return gradeMapper.toResponse(updatedGrade);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Excluindo nota ID: {}", id);

        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nota não encontrada com ID: " + id);
        }

        gradeRepository.deleteById(id);
        log.info("Nota excluída com ID: {}", id);
    }

    @Override
    @Transactional
    public GradeResponse bulkCreate(List<GradeRequest> requests) {
        log.info("Criando {} notas em lote", requests.size());

        List<Grade> grades = requests.stream()
                .map(request -> {
                    Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Avaliação não encontrada com ID: " + request.getAssessmentId()));

                    User student = userRepository.findById(request.getStudentUserId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Estudante não encontrado com ID: " + request.getStudentUserId()));

                    User gradedBy = null;
                    if (request.getGradedByUserId() != null) {
                        gradedBy = userRepository.findById(request.getGradedByUserId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Usuário não encontrado com ID: " + request.getGradedByUserId()));
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
        log.info("{} notas criadas em lote", savedGrades.size());

        // Retorna a primeira nota criada (ou poderia retornar todas)
        return savedGrades.isEmpty() ? null : gradeMapper.toResponse(savedGrades.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateStudentAverage(Long studentId, Long subjectId) {
        log.debug("Calculando média do estudante {} na disciplina {}", studentId, subjectId);
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
                            "Avaliação não encontrada com ID: " + request.getAssessmentId()));

            if (assessment.getMaxScore() != null &&
                    request.getScore().compareTo(assessment.getMaxScore()) > 0) {
                throw new IllegalArgumentException(
                        String.format("A nota (%s) não pode ser maior que a nota máxima da avaliação (%s)",
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
