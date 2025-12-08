package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.application.web.mapper.AssessmentMapper;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.AssessmentRepository;
import com.schoolagenda.domain.repository.SubjectRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final AssessmentMapper assessmentMapper;

    @Override
    @Transactional
    public AssessmentResponse create(AssessmentRequest request) {
        log.info("Criando nova avaliação: {}", request.getTitle());

        Subject subject = getSubjecyById(request);

        User createdBy = null;
        if (request.getCreatedByUserId() != null) {
            createdBy = userRepository.findById(request.getCreatedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuário não encontrado com ID: " + request.getCreatedByUserId()));
        }

        Assessment assessment = assessmentMapper.toEntity(request);
        assessment.setSubject(subject);
        assessment.setCreatedBy(createdBy);

        Assessment savedAssessment = assessmentRepository.save(assessment);
        log.info("Avaliação criada com ID: {}", savedAssessment.getId());

        return assessmentMapper.toResponse(savedAssessment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentResponse findById(Long id) {
        log.debug("Buscando avaliação com ID: {}", id);

        Assessment assessment = assessmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avaliação não encontrada com ID: " + id));

        return assessmentMapper.toResponse(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResponse> findAll() {
        log.debug("Buscando todas as avaliações");
        return assessmentRepository.findAll().stream()
                .map(assessmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResponse> findBySubject(Long subjectId) {
        log.debug("Buscando avaliações da disciplina ID: {}", subjectId);

        validateSubjectExists(subjectId);

        return assessmentRepository.findBySubjectId(subjectId).stream()
                .map(assessmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResponse> findPublishedBySubject(Long subjectId) {
        log.debug("Buscando avaliações publicadas da disciplina ID: {}", subjectId);

        validateSubjectExists(subjectId);

        return assessmentRepository.findBySubjectIdAndPublishedTrue(subjectId).stream()
                .map(assessmentMapper::toResponse)
                .toList();
    }

    @Override
//    @Transactional(readOnly = true)
//    public List<AssessmentResponse> findPublishedByFilters(Long subjectId, LocalDate startDate, LocalDate endDate) {
//        log.debug("Buscando avaliações publicadas com filtros");
//
//        validateSubjectExists(subjectId);
//
//        return assessmentRepository.findPublishedByFilters(subjectId, startDate, endDate).stream()
//                .map(assessmentMapper::toResponse)
//                .toList();
//    }
    // TODO: Método bem simples para este momento, pois estava dando muitos problemas, principalmente com um erro
    // no "Postgres". Assim que terminar os testes e verificar os "TODO's", refinar este método com todos os tipos
    // de validação!
    public List<AssessmentResponse> findPublishedByFilters(Long subjectId, LocalDate startDate, LocalDate endDate) {
        String startDateStr = startDate != null ? startDate.toString() : null;
        String endDateStr = endDate != null ? endDate.toString() : null;

        // TODO: Essa condicional é apenas provisória! Na refatoração, ficará com uma outra abordagem
        if(subjectId == null) {
            throw new BusinessResourceException("Provide the subject ID");
        }

        validateSubjectExists(subjectId);

        // Validação de datas
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessResourceException("Start date cannot be after end date");
        }

        // Se não passar data final, usa um limite (ex: 1 ano)
        if (startDateStr != null && endDateStr == null) {
            System.out.println("Data final null");
            endDateStr = startDate.plusYears(1).toString();
        }

        // Se não passar data inicial, usa início do ano letivo
        if (endDate != null && startDate == null) {
            startDateStr = LocalDate.now().withMonth(1).withDayOfMonth(1).toString(); // 01/01 do ano atual
            log.debug("DATA INICIAL NULL: VALOR DATA FINAL startDate={}", startDateStr);
        }

        return assessmentRepository.findPublishedByFiltersNative(subjectId, startDateStr, endDateStr)
                .stream()
                .map(assessmentMapper::toResponse)
                .toList();

//        log.debug("Buscando avaliações com filtros: subjectId={}, startDate={}, endDate={}",
//                subjectId, startDate, endDate);
//
//        validateSubjectExists(subjectId);
//
//        // Validação de datas
//        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
//            throw new BusinessResourceException("Start date cannot be after end date");
//        }
//
//        // Se não passar data final, usa um limite (ex: 1 ano)
//        if (startDateStr != null && endDateStr == null) {
//            System.out.println("Data final null");
//            endDateStr = startDate.plusYears(1).toString();
//        }
//
//        // Se não passar data inicial, usa início do ano letivo
//        if (endDate != null && startDate == null) {
//            endDateStr = LocalDate.now().withMonth(1).withDayOfMonth(1).toString(); // 01/01 do ano atual
//        }

//        return assessmentRepository.findPublishedByFilters(subjectId, startDate, endDate)
//                .stream()
//                .map(assessmentMapper::toResponse)
//                .toList();
//        return assessmentRepository.findPublishedByFiltersNative(subjectId, startDateStr, endDateStr)
//                .stream()
//                .map(assessmentMapper::toResponse)
//                .toList();




//        log.debug("Buscando avaliações com filtros: subjectId={}, startDate={}, endDate={}",
//                subjectId, startDate, endDate);
//
//        // Se todas as datas são null, busca sem filtro de data
//        if (startDate == null && endDate == null) {
//            return assessmentRepository.findByPublishedTrueAndSubjectId(subjectId)
//                    .stream()
//                    .map(assessmentMapper::toResponse)
//                    .toList();
//        }
//
//        // Se só subjectId é null, busca por data
//        if (subjectId == null && startDate != null && endDate != null) {
//            return assessmentRepository.findByPublishedTrueAndDueDateBetween(startDate, endDate)
//                    .stream()
//                    .map(assessmentMapper::toResponse)
//                    .toList();
//        }
//
//        // Caso completo
//        return assessmentRepository.findByPublishedTrueAndSubjectIdAndDueDateBetween(
//                        subjectId, startDate, endDate)
//                .stream()
//                .map(assessmentMapper::toResponse)
//                .toList();
    }

    // TODO: Método bem simples para este momento, pois estava dando muitos problemas, principalmente com um erro
    // no "Postgres". Assim que terminar os testes e verificar os "TODO's", refinar este método com os tipos
    // de validação!
    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResponse> findUpcoming(Integer days) {
        log.debug("Buscando avaliações dos próximos {} dias", days);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days != null ? days : 30);

        return assessmentRepository.findPublishedByFiltersNative(null, today.toString(), endDate.toString()).stream()
                .map(assessmentMapper::toResponse)
                .toList();
    }

    // TODO: verificar qual abordagem utilizar: o método "validateSubjectExists" ou "getSubjecyById"!
    private void validateSubjectExists(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
    }

    // TODO: verificar qual abordagem utilizar: o método "validateAssessmentExists" ou "getAssessmentById"!
    private void validateAssessmentExists(Long assessmentId) {
        if (!subjectRepository.existsById(assessmentId)) {
            throw new ResourceNotFoundException("Avaliação não encontrada com ID: " + assessmentId);
        }
    }

    @Override
    @Transactional
    public AssessmentResponse update(Long id, AssessmentRequest request) {
        log.info("Atualizando avaliação ID: {}", id);

        Assessment assessment = getAssessmentById(id);

        Subject subject = getSubjecyById(request);

        User createdBy = null;
        if (request.getCreatedByUserId() != null) {
            createdBy = userRepository.findById(request.getCreatedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with ID: " + request.getCreatedByUserId()));
        }

        assessmentMapper.updateEntity(request, assessment);
        assessment.setSubject(subject);
        assessment.setCreatedBy(createdBy);

        Assessment updatedAssessment = assessmentRepository.save(assessment);
        log.info("Assessment updated with ID: {}", id);

        return assessmentMapper.toResponse(updatedAssessment);
    }

    private Subject getSubjecyById(AssessmentRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with ID: " + request.getSubjectId()));
        return subject;
    }

    private Assessment getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment not found with ID: " + id));
        return assessment;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Excluindo avaliação ID: {}", id);

        if (!assessmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assessment not found with ID: " + id);
        }

        assessmentRepository.deleteById(id);
        log.info("Avaliação excluída com ID: {}", id);
    }

    @Override
    @Transactional
    public AssessmentResponse publish(Long id) {
        log.info("Publicando avaliação ID: {}", id);

        Assessment assessment = getAssessmentById(id);

        assessment.setPublished(true);
        Assessment updatedAssessment = assessmentRepository.save(assessment);

        log.info("Avaliação publicada com ID: {}", id);
        return assessmentMapper.toResponse(updatedAssessment);
    }

    @Override
    @Transactional
    public AssessmentResponse unpublish(Long id) {
        log.info("Despublicando avaliação ID: {}", id);

        Assessment assessment = getAssessmentById(id);

        assessment.setPublished(false);
        Assessment updatedAssessment = assessmentRepository.save(assessment);

        log.info("Avaliação despublicada com ID: {}", id);
        return assessmentMapper.toResponse(updatedAssessment);
    }
}
