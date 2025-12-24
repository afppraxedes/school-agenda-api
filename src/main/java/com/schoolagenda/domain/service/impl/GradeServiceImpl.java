package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.application.web.mapper.GradeMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.InvalidFilterException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Grade;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.*;
import com.schoolagenda.domain.service.GradeService;
import com.schoolagenda.domain.specification.GradeSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
    private final TeacherClassRepository teacherClassRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public GradeResponse create(GradeRequest request, AgendaUserDetails currentUser) {
        log.info("Iniciando criação de nota. Estudante User ID: {}, Avaliação ID: {}",
                request.getStudentUserId(), request.getAssessmentId());

        // 1. Validações de existência e busca de entidades
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada: " + request.getAssessmentId()));

        // Buscamos a entidade Student (não apenas User) para acessar a SchoolClass
        Student student = studentRepository.findByUserId(request.getStudentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Registro de estudante não encontrado para o usuário: " + request.getStudentUserId()));

        // 2. SEGURANÇA: Nível de Objeto (Se for Professor, validar vínculo com Turma e Disciplina)
        if (currentUser.hasRole(UserRole.TEACHER)) {
            validateTeacherPermission(currentUser.getId(), assessment, student);
        }

        // 3. Validação de Unicidade (Sua lógica atual)
        if (gradeRepository.existsByAssessmentIdAndStudentId(request.getAssessmentId(), request.getStudentUserId())) {
            throw new BusinessResourceException("Já existe uma nota registrada para este aluno nesta avaliação.");
        }

        // 4. Mapeamento e Regras de Negócio
        Grade grade = gradeMapper.toEntity(request);
        grade.setAssessment(assessment);
        grade.setStudent(student.getUser()); // Associa o User do aluno à Grade

        // Atribui quem está lançando a nota (Professor logado)
        User professor = userRepository.getReferenceById(currentUser.getId());
        grade.setGradedBy(professor);
        grade.setGradedAt(LocalDateTime.now());

        // Regra: Se ausente ou justificada, nota deve ser null (Sua lógica atual)
        if (Boolean.TRUE.equals(request.getAbsent()) || Boolean.TRUE.equals(request.getExcused())) {
            grade.setScore(null);
        }

        // Validação opcional: Nota não pode ser maior que o permitido na avaliação
        if (grade.getScore() != null && grade.getScore().compareTo(assessment.getMaxScore()) > 0) {
            throw new BusinessResourceException("A nota inserida (" + grade.getScore() +
                    ") é maior que o máximo permitido (" + assessment.getMaxScore() + ")");
        }

        Grade savedGrade = gradeRepository.save(grade);
        log.info("Nota criada com sucesso. ID: {}", savedGrade.getId());

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
    public GradeResponse update(Long id, GradeRequest request, AgendaUserDetails currentUser) {
        log.info("Iniciando atualização da nota ID: {}", id);

        // 1. Busca a nota existente
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada com ID: " + id));

        // 2. Busca a avaliação para validar nota máxima
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        // 3. Busca o estudante para validar a turma (Nível de Objeto)
        Student student = studentRepository.findByUserId(request.getStudentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        // 4. SEGURANÇA: Nível de Objeto para o Professor
        if (currentUser.hasRole(UserRole.TEACHER)) {
            validateTeacherPermission(currentUser.getId(), assessment, student);
        }

        // 5. Validação de Regra de Negócio: Nota Máxima
        if (request.getScore() != null && request.getScore().compareTo(assessment.getMaxScore()) > 0) {
            throw new BusinessResourceException("A nota não pode ser maior que o máximo da avaliação: " + assessment.getMaxScore());
        }

        // 6. Atualização dos campos via Mapper ou Manualmente
        // Usamos o mapper para atualizar a entidade existente para preservar metadados de criação
        gradeMapper.updateEntity(request, grade);

        // Re-vincula as entidades caso tenham mudado no Request (embora raro em update de nota)
        grade.setAssessment(assessment);
        grade.setStudent(student.getUser());

        // Atualiza quem editou por último
        User editor = userRepository.getReferenceById(currentUser.getId());
        grade.setGradedBy(editor);
        grade.setGradedAt(LocalDateTime.now());

        // Regra de ausência/justificativa
        if (Boolean.TRUE.equals(request.getAbsent()) || Boolean.TRUE.equals(request.getExcused())) {
            grade.setScore(null);
        }

        Grade updatedGrade = gradeRepository.save(grade);
        log.info("Nota ID {} atualizada com sucesso", id);

        return gradeMapper.toResponse(updatedGrade);
    }

//    @Override
//    @Transactional
//    public void delete(Long id) {
//        log.info("Deleting grade ID: {}", id);
//
//        if (!gradeRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Note not found with ID: " + id);
//        }
//
//        gradeRepository.deleteById(id);
//        log.info("Grade deleted with ID: {}", id);
//    }

    // Novo método adicionado pelo "Gemini"!
    @Override
    @Transactional
    public void delete(Long id, AgendaUserDetails currentUser) {
        log.info("Iniciando exclusão da nota ID: {} pelo usuário: {}", id, currentUser.getUsername());

        // 1. Busca a nota existente
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada com ID: " + id));

        // 2. SEGURANÇA: Nível de Objeto para o Professor
        // Somente professores autorizados ou cargos administrativos podem excluir
        if (currentUser.hasRole(UserRole.TEACHER)) {
            // Buscamos o registro do estudante para validar a turma
            Student student = studentRepository.findByUserId(grade.getStudent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estudante vinculado à nota não encontrado"));

            validateTeacherPermission(currentUser.getId(), grade.getAssessment(), student);
        }

        // 3. Validação de Período (Opcional/Sugestão)
        // Se a nota for de um bimestre já encerrado, você poderia impedir a exclusão aqui.

        gradeRepository.delete(grade);
        log.info("Nota ID {} excluída com sucesso", id);
    }

    // TODO: Rever esté método, pois com as alterações do "Gemini", tive que fazer algumas alterações!
    @Override
    @Transactional
    public GradeResponse bulkCreate(List<GradeRequest> requests, AgendaUserDetails currentUser) {
        log.info("Creating {} grades in batch", requests.size());

        List<Grade> grades = requests.stream()
                .map(request -> {
                    Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Assessment not found with ID: " + request.getAssessmentId()));

                    User student = userRepository.findById(request.getStudentUserId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Student not found with ID: " + request.getStudentUserId()));

                    // TODO: Rever esta condicional, pois o atributo "gradedByUserId" foi removido após as
                    // alterações sugeridas pelo "Gemini"
//                    User gradedBy = null;
//                    if (request.getGradedByUserId() != null) {
//                        gradedBy = userRepository.findById(request.getGradedByUserId())
//                                .orElseThrow(() -> new ResourceNotFoundException(
//                                        "User not found with ID: " + request.getGradedByUserId()));
//                    }

                    User editor = null; // userRepository.getReferenceById(currentUser.getId());
                    if (currentUser.getId() != null) {
                        editor = userRepository.findById(currentUser.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "User not found with ID: " + currentUser.getId()));
                    }

                    Grade grade = gradeMapper.toEntity(request);
                    grade.setAssessment(assessment);
                    grade.setStudent(student);
                    grade.setGradedBy(editor);
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

    private void validateTeacherPermission(Long teacherId, Assessment assessment, Student student) {
        // Verifica se o professor leciona a Disciplina da avaliação na Turma do aluno
        boolean isAuthorized = teacherClassRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
                teacherId,
                assessment.getSubject().getId(),
                student.getSchoolClass().getId()
        );

        if (!isAuthorized) {
            log.error("Acesso Negado: Professor {} tentou lançar nota para Aluno {} na Turma {}",
                    teacherId, student.getId(), student.getSchoolClass().getName());
            throw new AccessDeniedException("Você não tem permissão para lançar notas para esta turma/disciplina.");
        }
    }
//    private void validateTeacherPermission(Long teacherId, Long subjectId) {
//        List<Long> teacherSubjects = teacherClassRepository.findSubjectIdsByTeacherId(teacherId);
//        if (!teacherSubjects.contains(subjectId)) {
//            throw new AccessDeniedException("Você não tem permissão para lançar notas nesta disciplina.");
//        }
//    }

    // ========== MÉTODOS PAGINADOS ==========
    // TODO: Método anterior, sem o "RBAC" que o "Gemini" sugeriu
//    @Transactional(readOnly = true)
//    public PaginationResponse<GradeResponse> search(PaginationRequest pageRequest,
//                                                    GradeFilterRequest filter) {
//        log.debug("Buscando notas paginadas: {}", filter);
//
//        validateFilter(filter);
//        Specification<Grade> spec = buildSpecification(filter);
//
//        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
//        logSearchMetrics(page, filter);
//
//        return PaginationResponse.of(page.map(gradeMapper::toResponse));
//    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<GradeResponse> searchGrades(
            PaginationRequest pageRequest,
            GradeFilterRequest filter,
            AgendaUserDetails currentUser) {

        // 1. Inicia com a Specification baseada nos filtros de tela (nome, data, etc.)
        Specification<Grade> spec = GradeSpecifications.withFilters(filter);

        // 2. Adiciona a "Camada de Segurança" (Nível de Objeto)
        Specification<Grade> securitySpec;

        String string = currentUser.getAuthorities().toString();

        if (currentUser.hasRole(UserRole.STUDENT)) {// Aluno: vê apenas o seu ID
            securitySpec = GradeSpecifications.byStudentIds(List.of(currentUser.getId()));
        } else if (currentUser.hasRole(UserRole.RESPONSIBLE)) {// Responsável: vê apenas IDs dos filhos
            List<Long> childrenIds = teacherClassRepository.findStudentIdsByResponsibleId(currentUser.getId());
            securitySpec = GradeSpecifications.byStudentIds(childrenIds);
        } else if (currentUser.hasRole(UserRole.TEACHER)) {// Professor: vê apenas disciplinas que ele leciona
            List<Long> mySubjectIds = teacherClassRepository.findSubjectIdsByTeacherId(currentUser.getId());
            securitySpec = GradeSpecifications.bySubjectIds(mySubjectIds);
        } else if (currentUser.hasRole(UserRole.DIRECTOR) || currentUser.hasRole(UserRole.ADMINISTRATOR)) {// Sem restrições adicionais
            securitySpec = null;
        } else {
            securitySpec = (root, query, cb) -> cb.disjunction(); // Bloqueia tudo
        }

        // 3. Combina Filtros de Tela + Filtros de Segurança
        if (securitySpec != null) {
            spec = spec.and(securitySpec);
        }

        // 4. Executa a busca paginada
        Page<Grade> page = gradeRepository.findAll(spec, pageRequest.toPageable());
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
