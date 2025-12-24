package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.assessment.AssessmentFilterRequest;
import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.application.web.mapper.AssessmentMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.InvalidFilterException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.repository.AssessmentRepository;
import com.schoolagenda.domain.repository.SubjectRepository;
import com.schoolagenda.domain.repository.TeacherClassRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.AssessmentService;
import com.schoolagenda.domain.specification.AssessmentSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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
    private final TeacherClassRepository teacherClassRepository;

    @Override
    @Transactional
    public AssessmentResponse create(AssessmentRequest request) {
        log.info("Criando nova avaliação: {}", request.getTitle());

        Subject subject = getSubjecyById(request);

        // TODO: O código conentado abaixo é referente ao atributo "createdBy" que é do tipo "UserSimpleResponse", mas
        // agora estou implementando a "auditoria" e o "createdBy" é preenchido aitomaticamente com o email do
        // usuário logado!
//        User createdBy = null;
//        if (request.getCreatedByUserId() != null) {
//            createdBy = userRepository.findById(request.getCreatedByUserId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Usuário não encontrado com ID: " + request.getCreatedByUserId()));
//        }

        Assessment assessment = assessmentMapper.toEntity(request);
        assessment.setSubject(subject);
//        assessment.setCreatedBy(createdBy);

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

        // TODO: O código conentado abaixo é referente ao atributo "createdBy" que é do tipo "UserSimpleResponse", mas
        // agora estou implementando a "auditoria" e o "createdBy" é preenchido aitomaticamente com o email do
        // usuário logado!
//        User createdBy = null;
//        if (request.getCreatedByUserId() != null) {
//            createdBy = userRepository.findById(request.getCreatedByUserId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "User not found with ID: " + request.getCreatedByUserId()));
//        }

        assessmentMapper.updateEntity(request, assessment);
        assessment.setSubject(subject);
//        assessment.setCreatedBy(createdBy);

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

    // Paginação

    // Método atual corrigido pelo "Gemini" para o "RBAC"
    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AssessmentResponse> search(
            PaginationRequest pageRequest,
            AssessmentFilterRequest filter,
            AgendaUserDetails currentUser) { // <-- Adicionado o usuário

        // 1. Validações de Filtro (já existentes)
        if (filter != null && filter.hasDateRange() && !filter.isDateRangeValid()) {
            throw new InvalidFilterException("Data inicial não pode ser após data final");
        }

        // 2. Cria Specification base do filtro do usuário
        Specification<Assessment> filterSpec = (filter != null)
                ? AssessmentSpecifications.withFilters(filter)
                : Specification.allOf();

        // 3. CRIA O FILTRO DE SEGURANÇA (Nível de Objeto)
        Specification<Assessment> securitySpec = getSecuritySpecification(currentUser);

        // 4. Combina o filtro do usuário com o filtro de segurança
        Specification<Assessment> combinedSpec = filterSpec.and(securitySpec);

        // 5. Executa busca
        Page<Assessment> page = assessmentRepository.findAll(combinedSpec, pageRequest.toPageable());

        return PaginationResponse.of(page.map(assessmentMapper::toResponse));
    }

    // NOVO MÉTODO: Determina a Specification de segurança com base na Role
    private Specification<Assessment> getSecuritySpecification(AgendaUserDetails currentUser) {

        // Director e Administrator veem tudo: retorna um Specification vazio (allOf)
        if (currentUser.hasRole(UserRole.DIRECTOR) || currentUser.hasRole(UserRole.ADMINISTRATOR)) {
            return Specification.allOf();
        }

        // Teacher: Filtra avaliações por suas disciplinas
        if (currentUser.hasRole(UserRole.TEACHER)) {
            // Precisamos do ID das disciplinas que o professor leciona
            List<Long> subjectIds = teacherClassRepository.findSubjectIdsByTeacherId(currentUser.getId()); // <-- Query Nova no TeacherClassRepository!
            return AssessmentSpecifications.bySubjectIdIn(subjectIds); // <-- Novo método no AssessmentSpecifications
        }

        // STUDENT/RESPONSIBLE: Filtra por turmas dos alunos
        if (currentUser.hasRole(UserRole.STUDENT) || currentUser.hasRole(UserRole.RESPONSIBLE)) {
            // LÓGICA COMPLEXA: Requer JOINs com a turma do aluno (SchoolClass)
            // Isso depende de como a Entidade SchoolClass e Student estão ligadas.
            // A maneira mais simples é buscar os IDs das turmas/classes relevantes primeiro.

            // Por enquanto, vamos retornar uma Specification que não traga resultados para evitar vazamento de dados,
            // até que a lógica SchoolClass/Student seja integrada.
            // TODO: Implementar a lógica de SchoolClass/Student no Filtro.
//            return AssessmentSpecifications.byUserId(currentUser.getId()); // Query de teste/restrição
            return AssessmentSpecifications.createdBy(currentUser.getUsername());
        }

//        final var spec = Specification.where(null);

        // Perfil desconhecido ou sem permissão de leitura
//        return Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.disjunction()); // Retorna SEMPRE vazio
        // Perfil desconhecido ou sem permissão de leitura: Retorna SEMPRE vazio
        return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
    }

    // Método anterior
//    @Override
//    @Transactional(readOnly = true)
//    public PaginationResponse<AssessmentResponse> search(PaginationRequest pageRequest,
//                                                   AssessmentFilterRequest filter) {
//        log.debug("Buscando avaliações paginadas: {}", filter);
//
//        // Validação do filtro
//        if (filter != null && filter.hasDateRange() && !filter.isDateRangeValid()) {
//            throw new InvalidFilterException("Data inicial não pode ser após data final");
//        }
//
//        // Cria specification
//        Specification<Assessment> spec = (filter != null)
//                ? AssessmentSpecifications.withFilters(filter)
//                : Specification.allOf();
//
//        // Executa busca
//        Page<Assessment> page = assessmentRepository.findAll(spec, pageRequest.toPageable());
//
//        return PaginationResponse.of(page.map(assessmentMapper::toResponse));
//    }

    @Override
    // Métodos adicionais úteis
    @Transactional(readOnly = true)
    public PaginationResponse<AssessmentResponse> findPublished(PaginationRequest pageRequest) {
        Specification<Assessment> spec = AssessmentSpecifications.isPublished();
        Page<Assessment> page = assessmentRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(assessmentMapper::toResponse));
    }


    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AssessmentResponse> findBySubject(PaginationRequest pageRequest,
                                                                Long subjectId) {
        Specification<Assessment> spec = AssessmentSpecifications.bySubject(subjectId)
                .and(AssessmentSpecifications.isPublished());

        Page<Assessment> page = assessmentRepository.findAll(spec, pageRequest.toPageable());
        return PaginationResponse.of(page.map(assessmentMapper::toResponse));
    }

    // Para validação de filtros para "grade"
    private void validateFilter(AssessmentFilterRequest filter) {
        if (filter == null) return;

        // Valida datas
        if (filter.hasDateRange() && filter.getDueDateFrom().isAfter(filter.getDueDateTo())) {
            throw new InvalidFilterException("Data inicial não pode ser após data final");
        }

        // Valida título muito curto
        if (filter.hasTitle() && filter.getTitle().trim().length() < 2) {
            throw new InvalidFilterException("Título deve ter pelo menos 2 caracteres");
        }
    }
}
