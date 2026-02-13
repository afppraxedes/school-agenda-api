package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.attendance.AttendanceSummary;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.application.web.dto.response.ReportCardResponse;
import com.schoolagenda.application.web.dto.response.SubjectSummaryResponse;
import com.schoolagenda.application.web.mapper.GradeMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.AcademicStatus;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.InvalidFilterException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.*;
import com.schoolagenda.domain.repository.*;
import com.schoolagenda.domain.service.AttendanceService;
import com.schoolagenda.domain.service.GradeService;
import com.schoolagenda.domain.service.StudentService;
import com.schoolagenda.domain.specification.GradeSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ResponsibleStudentRepository responsibleStudentRepository;
    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;

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
        grade.setGradedAt(OffsetDateTime.now(ZoneOffset.UTC));

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
        grade.setGradedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // Regra de ausência/justificativa
        if (Boolean.TRUE.equals(request.getAbsent()) || Boolean.TRUE.equals(request.getExcused())) {
            grade.setScore(null);
        }

        Grade updatedGrade = gradeRepository.save(grade);
        log.info("Nota ID {} atualizada com sucesso", id);

        return gradeMapper.toResponse(updatedGrade);
    }

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
        log.info("Processando {} notas em lote", requests.size());

        // 1. Validar se o ID do professor logado existe
        if (currentUser.getId() == null) {
            throw new ResourceNotFoundException("ID do usuário autenticado não encontrado.");
        }
        User editor = userRepository.getReferenceById(currentUser.getId());

        List<Grade> gradesToSave = requests.stream().map(request -> {
            // Validação defensiva dos IDs do Request
            if (request.getAssessmentId() == null || request.getStudentUserId() == null) {
                throw new BusinessResourceException("AssessmentId e StudentUserId são obrigatórios para cada nota do lote.");
            }

            // 2. Busca nota existente (UPSERT)
            // Certifique-se que seu repository usa (Long assessmentId, Long studentUserId)
            Grade grade = gradeRepository.findByAssessmentIdAndStudentId(
                            request.getAssessmentId(), request.getStudentUserId())
                    .orElseGet(() -> {
                        log.debug("Criando nova nota para estudante {}", request.getStudentUserId());
                        return gradeMapper.toEntity(request);
                    });

            // 3. Se for atualização, o Mapper preenche os novos valores (score, feedback, etc)
            if (grade.getId() != null) {
                gradeMapper.updateEntity(request, grade);
            }

            // 4. Vinculação Manual das Entidades (Garante que os IDs não sejam nulos)
            grade.setAssessment(assessmentRepository.getReferenceById(request.getAssessmentId()));

            // IMPORTANTE: Aqui usamos o ID do User do estudante, conforme seu mapeamento na entidade Grade
            grade.setStudent(userRepository.getReferenceById(request.getStudentUserId()));

            grade.setGradedBy(editor);
            grade.setGradedAt(OffsetDateTime.now(ZoneOffset.UTC));

            // Aplica regra de ausência
            if (Boolean.TRUE.equals(request.getAbsent()) || Boolean.TRUE.equals(request.getExcused())) {
                grade.setScore(null);
            }

            return grade;
        }).toList();

        gradeRepository.saveAll(gradesToSave);

        // Identifica alunos únicos afetados no lote
        Set<Long> affectedStudentUserIds = requests.stream()
                .map(GradeRequest::getStudentUserId)
                .collect(Collectors.toSet());

        // Dispara atualização da média para cada um
        affectedStudentUserIds.forEach(userId -> {
            BigDecimal newAverage = gradeRepository.findAverageByStudentUserId(userId);
            studentService.updateGlobalAverage(userId, newAverage);
        });

        // Em operações de lote, retornar a primeira ou um objeto vazio é comum
        return null;
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

    // BOLETIM ESCOLAR DO ESTUDANTE]
    @Override
    @Transactional(readOnly = true)
    public ReportCardResponse getStudentReportCard(Long studentUserId, AgendaUserDetails currentUser) {
        validateReportCardAccess(studentUserId, currentUser);

        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        // 1. Queries Otimizadas (Apenas 2 idas ao banco)
        List<Grade> allGrades = gradeRepository.findAllByStudentId(studentUserId);
        Map<Long, AttendanceSummary> attendanceMap = attendanceRepository.findAttendanceSummariesByStudent(studentUserId)
                .stream()
                .collect(Collectors.toMap(AttendanceSummary::subjectId, att -> att));

        // 2. Agrupamento por Disciplina
        Map<Subject, List<Grade>> gradesBySubject = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getAssessment().getSubject()));

        // 3. Transformação em Resumos
        List<SubjectSummaryResponse> subjectSummaries = gradesBySubject.entrySet().stream()
                .map(entry -> calculateSubjectAverage(entry.getKey(), entry.getValue(), studentUserId,
                        attendanceMap.getOrDefault(entry.getKey().getId(), AttendanceSummary.empty(entry.getKey().getId()))))
                .toList();

        // 4. Média Global e Status
        BigDecimal globalAverage = calculateGlobalAverage(subjectSummaries);
        AcademicStatus globalStatus = determineGlobalStatus(subjectSummaries);

        return new ReportCardResponse(
                student.getUser().getId(),
                student.getFullName(),
                student.getSchoolClass().getName(),
                subjectSummaries,
                globalAverage,
                globalStatus
        );
    }

    private void validateReportCardAccess(Long studentUserId, AgendaUserDetails currentUser) {
        // 1. Se for ADMINISTRADOR ou DIRETOR, o acesso é irrestrito
        if (currentUser.hasRole(UserRole.ADMINISTRATOR) || currentUser.hasRole(UserRole.DIRECTOR)) {
            return;
        }

        // 2. Se for PROFESSOR, ele pode ver o boletim (opcional, dependendo da sua regra)
        // Caso queira restringir para apenas professores daquele aluno, adicionar lógica extra aqui.
        if (currentUser.hasRole(UserRole.TEACHER)) {
            // Verifica se o professor leciona em QUALQUER turma que este aluno pertença
            boolean hasVoucher = teacherClassRepository.existsTeacherLinkWithStudent(currentUser.getId(), studentUserId);
            if (!hasVoucher) {
                throw new AccessDeniedException("Você não tem permissão para ver o boletim deste aluno pois não leciona para a turma dele.");
            }
            return;
        }

        // 3. Se for ESTUDANTE, ele só pode ver o seu próprio ID
        if (currentUser.hasRole(UserRole.STUDENT)) {
            if (!currentUser.getId().equals(studentUserId)) {
                log.warn("Acesso negado: Aluno {} tentou acessar boletim do aluno {}", currentUser.getId(), studentUserId);
                throw new AccessDeniedException("Você só tem permissão para visualizar o seu próprio boletim.");
            }
            return;
        }

        // 4. Se for RESPONSÁVEL, deve haver vínculo na tabela 'responsible_student'
        if (currentUser.hasRole(UserRole.RESPONSIBLE)) {
            boolean isLinked = responsibleStudentRepository.existsByResponsibleIdAndStudentUserId(
                    currentUser.getId(), studentUserId);

            if (!isLinked) {
                log.warn("Acesso negado: Responsável {} tentou acessar boletim não vinculado do aluno {}",
                        currentUser.getId(), studentUserId);
                throw new AccessDeniedException("Você não possui permissão para visualizar os dados deste estudante.");
            }
        }
    }

    private BigDecimal calculateGlobalAverage(List<SubjectSummaryResponse> subjectSummaries) {
        if (subjectSummaries == null || subjectSummaries.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumOfAverages = subjectSummaries.stream()
                .map(SubjectSummaryResponse::average)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sumOfAverages.divide(
                BigDecimal.valueOf(subjectSummaries.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    // NOVA VERSÃO DO MÉTODO COM A LÓGICA DE FREQUÊNCIA INCLUÍDA E PONTOS NECESSÁRIOS
    private SubjectSummaryResponse calculateSubjectAverage(Subject subject, List<Grade> grades, Long studentId) {
        BigDecimal minPassAverage = new BigDecimal("6.0");

        // 1. CHAMA O CÁLCULO DE NOTAS (Média Ponderada + Recuperação)
        BigDecimal average = performWeightedAverageCalculation(grades);

        // 2. BUSCA DADOS DE FREQUÊNCIA NO SERVICE
        long totalClasses = attendanceService.countByStudentIdAndSubjectId(studentId, subject.getId());
        long totalAbsences = attendanceService.countByStudentIdAndSubjectIdAndPresentFalse(studentId, subject.getId());

        // 3. CHAMA O CÁLCULO DE FREQUÊNCIA (%)
        BigDecimal attendancePercentage = calculateAttendancePercentage(totalClasses, totalAbsences);

        // 4. VALIDAÇÕES DE STATUS E APROVAÇÃO
        boolean isApprovedByAttendance = attendancePercentage.compareTo(new BigDecimal("75.0")) >= 0;
        AcademicStatus status = determineStatus(average, attendancePercentage, grades.isEmpty());

        // 5. CAMPOS AUXILIARES PARA O ALUNO
        BigDecimal pointsNeeded = minPassAverage.subtract(average).max(BigDecimal.ZERO);
        boolean canDoRecovery = (status == AcademicStatus.RECUPERACAO || status == AcademicStatus.REPROVADO)
                && isApprovedByAttendance;

        return new SubjectSummaryResponse(
                subject.getId(),
                subject.getName(),
                grades.stream().map(gradeMapper::toDetailResponse).toList(),
                average,
                status,
                pointsNeeded,
                canDoRecovery,
                attendancePercentage,
                totalAbsences,
                isApprovedByAttendance,
                subject.getTeacher().getName() // incluso no resumo (eu que incluí esse campo)
        );
    }

    // Responsável por realizar o cálculo matemático da assiduidade, tratando o cenário onde ainda não houve
    // aulas registradas (evitando divisão por zero).
    private BigDecimal calculateAttendancePercentage(long totalClasses, long totalAbsences) {
        if (totalClasses <= 0) {
            return BigDecimal.valueOf(100.0); // Se não houve aulas, a frequência é 100%
        }

        long presences = totalClasses - totalAbsences;
        return BigDecimal.valueOf(presences)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalClasses), 2, RoundingMode.HALF_UP);
    }

    // Responsável por realizar o cálculo matemático da média ponderada, aplicando a lógica de recuperação.
    // Centraliza a matemática das notas e a lógica de recuperação.
    private BigDecimal performWeightedAverageCalculation(List<Grade> grades) {
        // Separação limpa usando particionamento
        Map<Boolean, List<Grade>> partitioned = grades.stream()
                .collect(Collectors.partitioningBy(g -> g.getAssessment().isRecovery()));

        List<Grade> regularGrades = partitioned.get(false);
        Optional<Grade> recovery = partitioned.get(true).stream()
                .filter(g -> g.getScore() != null).findFirst();

        // Lógica de substituição imutável (não altera a entidade, apenas para o cálculo)
        recovery.ifPresent(rec -> {
            regularGrades.stream()
                    .filter(g -> g.getScore() != null)
                    .min(Comparator.comparing(Grade::getScore))
                    .ifPresent(lowest -> {
                        if (rec.getScore().compareTo(lowest.getScore()) > 0) {
                            lowest.setScore(rec.getScore());
                        }
                    });
        });

        return calculateFinalWeightedMean(regularGrades);
    }

    // NOVA VERSÃO DO MÉTODO COM A LÓGICA DE RECUPERAÇÃO REVISADA
    private AcademicStatus determineStatus(BigDecimal average, BigDecimal attendance, boolean noGrades) {
        if (noGrades) return AcademicStatus.EM_CURSO;

        boolean hasGrade = average.compareTo(new BigDecimal("6.0")) >= 0;
        boolean hasAttendance = attendance.compareTo(new BigDecimal("75.0")) >= 0;

        if (hasGrade && hasAttendance) return AcademicStatus.APROVADO;
        if (!hasAttendance) return AcademicStatus.REPROVADO; // Reprovado por falta direto
        if (average.compareTo(new BigDecimal("4.0")) >= 0) return AcademicStatus.RECUPERACAO;

        return AcademicStatus.REPROVADO;
    }

    /**
     * Valida se o usuário logado (Responsável) possui vínculo com o aluno solicitado.
     * Lança AccessDeniedException caso o vínculo não exista.
     */
    private void validateRelationship(Long responsibleUserId, Long studentUserId) {
        boolean isLinked = responsibleStudentRepository.existsByResponsibleIdAndStudentUserId(
                responsibleUserId, studentUserId);

        if (!isLinked) {
            log.warn("Tentativa de acesso não autorizada: Usuário {} tentou acessar boletim do aluno {}",
                    responsibleUserId, studentUserId);
            throw new AccessDeniedException("Você não possui permissão para visualizar os dados deste estudante.");
        }
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

    // ========== MÉTODOS PRIVADOS AUXILIARES ==========

    private void updateStudentGlobalAverage(Long userId) {
        Double average = gradeRepository.calculateAverageByStudentUserId(userId);

        // Supondo que você tenha um campo globalAverage na tabela/entidade Student ou em um DashboardDTO
        studentRepository.updateGlobalAverage(userId, BigDecimal.valueOf(average != null ? average : 0.0));
    }
    /**
     * Realiza o cálculo matemático da média ponderada final.
     * Soma (nota * peso) / soma(pesos)
     */
    private BigDecimal calculateFinalWeightedMean(List<Grade> grades) {
        BigDecimal totalPoints = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (Grade grade : grades) {
            if (grade.getScore() != null) {
                BigDecimal weight = grade.getAssessment().getWeight();

                // totalPoints += (score * weight)
                totalPoints = totalPoints.add(grade.getScore().multiply(weight));

                // totalWeight += weight
                totalWeight = totalWeight.add(weight);
            }
        }

        // Evita divisão por zero se não houver notas ou pesos configurados
        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        // Média = Pontos Totais / Peso Total
        return totalPoints.divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    private SubjectSummaryResponse calculateSubjectAverage(Subject subject, List<Grade> grades, Long studentId, AttendanceSummary att) {
        BigDecimal minPassAverage = new BigDecimal("6.0");

        // Cálculo da média ponderada (já incluindo lógica de recuperação)
        BigDecimal average = performWeightedAverageCalculation(grades);

        // Cálculo da porcentagem de frequência
        BigDecimal attendancePercentage = calculateAttendancePercentage(att.totalClasses(), att.totalAbsences());
        boolean isApprovedByAttendance = attendancePercentage.compareTo(new BigDecimal("75.0")) >= 0;

        // Determinação do Status Acadêmico (Nota + Presença)
        AcademicStatus status = determineStatus(average, attendancePercentage, grades.isEmpty());

        // Auxiliares para o Front-end
        BigDecimal pointsNeeded = minPassAverage.subtract(average).max(BigDecimal.ZERO);
        boolean canDoRecovery = (status == AcademicStatus.RECUPERACAO || status == AcademicStatus.REPROVADO) && isApprovedByAttendance;

        return new SubjectSummaryResponse(
                subject.getId(),
                subject.getName(),
                grades.stream().map(gradeMapper::toDetailResponse).toList(),
                average,
                status,
                pointsNeeded,
                canDoRecovery,
                attendancePercentage,
                att.totalAbsences(),
                isApprovedByAttendance,
                "Professor da Disciplina" // Aqui você poderia buscar o nome do professor via TeacherClass se desejar
        );
    }

    private AcademicStatus determineGlobalStatus(List<SubjectSummaryResponse> subjects) {
        if (subjects.isEmpty()) return AcademicStatus.EM_CURSO;

        // Se estiver reprovado em qualquer matéria, o status global é afetado
        boolean hasFail = subjects.stream().anyMatch(s -> s.status() == AcademicStatus.REPROVADO);
        boolean hasRecovery = subjects.stream().anyMatch(s -> s.status() == AcademicStatus.RECUPERACAO);

        if (hasFail) return AcademicStatus.REPROVADO;
        if (hasRecovery) return AcademicStatus.RECUPERACAO;

        return AcademicStatus.APROVADO;
    }

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
