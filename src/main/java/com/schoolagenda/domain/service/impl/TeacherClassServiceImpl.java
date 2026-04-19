package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.GradeStudentDTO;
import com.schoolagenda.application.web.dto.ReportCardGradeDTO;
import com.schoolagenda.application.web.dto.request.SaveGradeRequest;
import com.schoolagenda.application.web.dto.request.SingleStudentGradesRequest;
import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.ActiveClassResponse;
import com.schoolagenda.application.web.dto.response.PerformanceHistoryResponse;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.application.web.mapper.TeacherClassMapper;
import com.schoolagenda.domain.exception.BusinessException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.*;
import com.schoolagenda.domain.repository.*;
import com.schoolagenda.domain.service.PushSubscriptionService;
import com.schoolagenda.domain.service.TeacherClassService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherClassServiceImpl implements TeacherClassService {

    private final TeacherClassRepository teacherClassRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final TeacherClassMapper teacherClassMapper;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AssessmentRepository assessmentRepository;
    private final PushSubscriptionService pushService;
    private static final Logger logger = LoggerFactory.getLogger(TeacherClassServiceImpl.class);

    @Override
    @Transactional
    public TeacherClassResponse createTeacherClass(TeacherClassRequest request) {
        // Verifica se o vínculo já existe
        if (teacherClassRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
                request.teacherId(), request.subjectId(), request.schoolClassId())) {
            throw new RuntimeException("Professor já está vinculado a esta disciplina nesta turma");
        }

        // Busca o professor
        User teacher = userRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + request.teacherId()));

        // Verifica se o usuário é professor
        if (!isUserTeacher(teacher)) {
            throw new RuntimeException("Usuário não é um professor");
        }

        // Busca a disciplina
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + request.subjectId()));

        // Busca a turma
        SchoolClass schoolClass = schoolClassRepository.findById(request.schoolClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + request.schoolClassId()));

        // Cria e salva o vínculo
        TeacherClass teacherClass = new TeacherClass(teacher, subject, schoolClass);
        TeacherClass savedTeacherClass = teacherClassRepository.save(teacherClass);

        return teacherClassMapper.toResponse(savedTeacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherClassResponse getTeacherClassById(Long id) {
        TeacherClass teacherClass = teacherClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo professor-disciplina-turma não encontrado com ID: " + id));

        return teacherClassMapper.toResponse(teacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getClassesByTeacher(Long teacherId) {
        List<TeacherClass> teacherClasses = teacherClassRepository.findByTeacherId(teacherId);
        return teacherClasses.stream()
                .map(teacherClassMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getTeachersByClass(Long schoolClassId) {
        // Busca todos os vínculos para uma turma específica
        List<TeacherClass> teacherClasses = teacherClassRepository.findBySchoolClassId(schoolClassId);
        return teacherClasses.stream()
                .map(teacherClassMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherClassResponse getTeacherClass(Long teacherId, Long subjectId, Long schoolClassId) {
        TeacherClass teacherClass = teacherClassRepository
                .findByTeacherIdAndSubjectIdAndSchoolClassId(teacherId, subjectId, schoolClassId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo não encontrado para professor: " + teacherId +
                                ", disciplina: " + subjectId + " e turma: " + schoolClassId));

        return teacherClassMapper.toResponse(teacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean teacherClassExists(Long teacherId, Long subjectId, Long schoolClassId) {
        return teacherClassRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
                teacherId, subjectId, schoolClassId);
    }

    @Override
    @Transactional
    public TeacherClassResponse updateTeacherClass(Long id, TeacherClassRequest request) {
        TeacherClass teacherClass = teacherClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo professor-disciplina-turma não encontrado com ID: " + id));

        // Verifica se o novo vínculo criaria um duplicado
        if (!teacherClass.getTeacher().getId().equals(request.teacherId()) ||
                !teacherClass.getSubject().getId().equals(request.subjectId()) ||
                !teacherClass.getSchoolClass().getId().equals(request.schoolClassId())) {

            if (teacherClassRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
                    request.teacherId(), request.subjectId(), request.schoolClassId())) {
                throw new RuntimeException("Professor já está vinculado a esta disciplina nesta turma");
            }
        }

        // Atualiza professor se mudou
        if (!teacherClass.getTeacher().getId().equals(request.teacherId())) {
            User teacher = userRepository.findById(request.teacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + request.teacherId()));

            if (!isUserTeacher(teacher)) {
                throw new RuntimeException("Usuário não é um professor");
            }

            teacherClass.setTeacher(teacher);
        }

        // Atualiza disciplina se mudou
        if (!teacherClass.getSubject().getId().equals(request.subjectId())) {
            Subject subject = subjectRepository.findById(request.subjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + request.subjectId()));
            teacherClass.setSubject(subject);
        }

        // Atualiza turma se mudou
        if (!teacherClass.getSchoolClass().getId().equals(request.schoolClassId())) {
            SchoolClass schoolClass = schoolClassRepository.findById(request.schoolClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + request.schoolClassId()));
            teacherClass.setSchoolClass(schoolClass);
        }

        TeacherClass updatedTeacherClass = teacherClassRepository.save(teacherClass);
        return teacherClassMapper.toResponse(updatedTeacherClass);
    }

    @Override
    @Transactional
    public void deleteTeacherClass(Long id) {
        if (!teacherClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vínculo professor-disciplina-turma não encontrado com ID: " + id);
        }
        teacherClassRepository.deleteById(id);  // Deleção otimizada por ID
    }

    @Override
    @Transactional
    public void deleteTeacherClass(Long teacherId, Long subjectId, Long schoolClassId) {
        TeacherClass teacherClass = teacherClassRepository
                .findByTeacherIdAndSubjectIdAndSchoolClassId(teacherId, subjectId, schoolClassId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo não encontrado para professor: " + teacherId +
                                ", disciplina: " + subjectId + " e turma: " + schoolClassId));

        teacherClassRepository.delete(teacherClass);  // Deleção por objeto
    }

    @Override
    @Transactional(readOnly = true)
    public long getClassCountByTeacher(Long teacherId) {
        return teacherClassRepository.countByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAllDistinctSchoolClassIds() {
        // Alterado: agora retorna IDs de turmas distintas
        return teacherClassRepository.findDistinctSchoolClassIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findStudentIdsByResponsibleId(Long responsibleId) {
        return teacherClassRepository.findStudentIdsByResponsibleId(responsibleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findSubjectIdsByTeacherId(Long teacherId) {
        return teacherClassRepository.findSubjectIdsByTeacherId(teacherId);
    }

    public List<ActiveClassResponse> findClassesByTeacherEmail(String email) {
        return teacherClassRepository.findByTeacherEmail(email)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca alunos e suas notas para uma turma/disciplina específica.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GradeStudentDTO> getStudentsGrades(Long teacherClassId) {
        TeacherClass tc = teacherClassRepository.findById(teacherClassId)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo não encontrado"));

        // 1. Buscamos TODOS os alunos da turma (independente de terem nota)
        List<Student> students = studentRepository.findBySchoolClassIdOrderByFullNameAsc(tc.getSchoolClass().getId());

        return students.stream().map(s -> {
//            // 2. Buscamos as notas que já existem para este aluno nesta disciplina
//            List<Grade> grades = gradeRepository.findTop4ByStudentIdAndSubjectId(s.getUser().getId(), tc.getSubject().getId());
//
//            // 3. Mapeamento SEGURO: Se a lista for menor que 4, usamos 0.0
//            // Isso evita o erro de IndexOutOfBounds e permite o primeiro lançamento
//            BigDecimal g1 = getScoreFromList(grades, 0);
//            BigDecimal g2 = getScoreFromList(grades, 1);
//            BigDecimal g3 = getScoreFromList(grades, 2);
//            BigDecimal g4 = getScoreFromList(grades, 3);

            List<Grade> studentGrades = gradeRepository.findGradesForTeacherClass(s.getUser().getId(), teacherClassId);

            // Mapeamento posicional garantido pela ordenação do SQL
            BigDecimal g1 = studentGrades.size() > 0 ? studentGrades.get(0).getScore() : BigDecimal.ZERO;
            BigDecimal g2 = studentGrades.size() > 1 ? studentGrades.get(1).getScore() : BigDecimal.ZERO;
            BigDecimal g3 = studentGrades.size() > 2 ? studentGrades.get(2).getScore() : BigDecimal.ZERO;
            BigDecimal g4 = studentGrades.size() > 3 ? studentGrades.get(3).getScore() : BigDecimal.ZERO;

            // 4. Média Ponderada ou Aritmética
            BigDecimal avg = (g1.add(g2).add(g3).add(g4)).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);

            return new GradeStudentDTO(
                    s.getId(),
                    s.getFullName(),
                    g1, g2, g3, g4,
                    avg,
                    s.getFeedback()
//                    studentGrades.isEmpty() ? "" : studentGrades.get(0).getFeedback()
                    // TODO: implementar os campos "isAbsent" e "isExcused" mais pra frente quando for refatorar!
                    // studentGrades.isEmpty() ? false : studentGrades.get(0).getExcused()
//                    grades.isEmpty() ? "" : grades.get(0).getFeedback()
//                    grades.isEmpty() ? false : grades.get(0).getAbsent(),
//                    grades.isEmpty() ? false : grades.get(0).getExcused()
            );
        }).toList();
    }

    @Override
    @Transactional
    public void saveAllGrades(List<SaveGradeRequest> requests) {
        for (SaveGradeRequest req : requests) {
            // 1. Busca as 4 avaliações da disciplina ordenadas por data (DETERMINÍSTICO)
            List<Assessment> assessments = assessmentRepository.findByTeacherClassIdOrderByDueDateAsc(req.teacherClassId());

            // 2. Mapeia cada nota do DTO para a avaliação correspondente
            updateOrCreateGrade(req.studentId(), assessments.get(0).getId(), req.grade1());
            updateOrCreateGrade(req.studentId(), assessments.get(1).getId(), req.grade2());
            updateOrCreateGrade(req.studentId(), assessments.get(2).getId(), req.grade3());
            updateOrCreateGrade(req.studentId(), assessments.get(3).getId(), req.grade4());

            // 3. Atualiza o feedback (geralmente vinculado ao registro do estudante ou à última nota)
//            studentRepository.updateFeedback(req.studentId(), req.feedback());
            Student student = studentRepository.findByUserId(req.studentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));
            student.setFeedback(req.feedback());
            // Não precisa de save() explícito se estiver em @Transactional, mas pode usar por clareza.

            studentRepository.save(student);
        }
    }

    @Override
    @Transactional
    public void saveAllStudentGrades(List<SingleStudentGradesRequest> requests) {
        if (requests == null || requests.isEmpty()) return;

        for (SingleStudentGradesRequest req : requests) {
            try {
                // Centralizamos toda a lógica aqui
                this.updateStudentGrades(req);
            } catch (Exception e) {
                logger.error("Falha fatal no lote: Aluno {}", req.studentId());
                throw new BusinessException("Erro ao processar lote de notas: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void updateStudentGrades(SingleStudentGradesRequest request) {
        // 1. Buscamos o Estudante pela PK (ID 1)
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com ID: " + request.studentId()));

        // 2. Agora temos o User ID correto (23) para as notas
        Long userId = student.getUser().getId();

        // 3. Busca os Assessments (B1-B4)
        List<Assessment> assessments = assessmentRepository.findByTeacherClassIdOrderByDueDateAsc(request.teacherClassId());

        BigDecimal[] scores = { request.grade1(), request.grade2(), request.grade3(), request.grade4() };

        for (int i = 0; i < scores.length; i++) {
            final int index = i;
            // Usamos o userId (23) que extraímos do objeto student
            Grade grade = gradeRepository.findByStudentIdAndAssessmentId(userId, assessments.get(index).getId())
                    .orElseGet(() -> {
                        Grade newGrade = new Grade();
                        newGrade.setStudent(student.getUser()); // Associa o User correto
                        newGrade.setAssessment(assessments.get(index));
                        newGrade.setCreatedBy("system");
                        return newGrade;
                    });

            grade.setScore(scores[index] != null ? scores[index] : BigDecimal.ZERO);
            grade.setLastModifiedBy("system");
            gradeRepository.save(grade);
        }

        // 4. Atualiza o feedback e a média global usando o objeto que já temos em mãos
        student.setFeedback(request.feedback());
        BigDecimal newAvg = gradeRepository.calculateAverageByUserId(userId);
        student.setGlobalAverage(newAvg != null ? newAvg : BigDecimal.ZERO);

        studentRepository.save(student);
    }

    private void updateStudentMetadata(Long userId, String feedback) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        student.setFeedback(feedback);

        // Calcula a nova média global
        BigDecimal newAvg = gradeRepository.calculateAverageByUserId(userId);
        student.setGlobalAverage(newAvg != null ? newAvg : BigDecimal.ZERO);

        studentRepository.save(student);
    }

    /**
     * Método auxiliar (Guts) para persistência individual
     */
    private void updateOrCreateGrade(Long studentId, Long assessmentId, BigDecimal score) {
        Grade grade = gradeRepository.findByStudentIdAndAssessmentId(studentId, assessmentId)
                .orElse(new Grade());

        if (grade.getId() == null) {
            // Configuração de novo registro (Seed/First Save)
            grade.setStudent(userRepository.getReferenceById(studentId));
            grade.setAssessment(assessmentRepository.getReferenceById(assessmentId));
            grade.setCreatedBy("system_teacher");
            grade.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        }

        grade.setScore(score != null ? score : BigDecimal.ZERO);
        grade.setLastModifiedBy("system_teacher");
        grade.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        gradeRepository.save(grade);
    }

    private void updateStudentSummary(Long studentId, String feedback) {
        Student student = studentRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        student.setFeedback(feedback);

        // Recalcula a média global baseada em todas as notas do aluno
        BigDecimal newAverage = gradeRepository.calculateWeightedAverage(studentId);
        student.setGlobalAverage(newAverage != null ? newAverage : BigDecimal.ZERO);

        studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceHistoryResponse getStudentHistory(Long studentId, Long teacherClassId) {
        // 1. Busca o estudante e extrai o userId (vínculo real na tabela de grades)
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com ID: " + studentId));

        // Como as notas estão vinculadas ao User (ID 23, por exemplo), extraímos aqui
        Long userId = student.getUser().getId();

        // 2. Busca as notas específicas para este vínculo (Turma + Disciplina)
        // O método findGradesForTeacherClass já garante a ordenação por dueDate ASC
        List<Grade> grades = gradeRepository.findGradesForTeacherClass(userId, teacherClassId);

        // 3. Mapeia as notas para uma lista de exatamente 4 valores (B1 a B4)
        List<BigDecimal> values = grades.stream()
                .map(g -> g.getScore() != null ? g.getScore() : BigDecimal.ZERO)
                .limit(4)
                .collect(Collectors.toList());

        // Garante que a lista tenha 4 elementos para o gráfico não quebrar no Frontend
        while (values.size() < 4) {
            values.add(BigDecimal.ZERO);
        }

        // 4. Lógica de Recuperação: Média < 6.0
        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        // Usamos HALF_UP para arredondar 5.995 para 6.00 de forma justa
        BigDecimal average = sum.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
        boolean recovery = average.compareTo(new BigDecimal("6.0")) < 0;

        return new PerformanceHistoryResponse(
                student.getFullName(),
                List.of("B1", "B2", "B3", "B4"),
                values,
                recovery
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportCardGradeDTO> listGradesByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        Long classId = student.getSchoolClass().getId();

        // 1. Busca as disciplinas vinculadas a esta turma específica
        List<TeacherClass> teacherClasses = teacherClassRepository.findBySchoolClassId(classId);

        // LOG DE DEBUG: Verifique se este log aparece no console com valor > 0
        logger.info("📄 Gerando boletim para o aluno {}. Turma ID: {}. Disciplinas encontradas: {}",
                student.getFullName(), classId, teacherClasses.size());

        return teacherClasses.stream().map(tc -> {
            // 2. Busca as notas do User (ID 23) para a Disciplina do vínculo
            List<Grade> grades = gradeRepository.findTop4ByStudentIdAndSubjectId(
                    student.getUser().getId(),
                    tc.getSubject().getId()
            );

            BigDecimal b1 = getScoreFromList(grades, 0);
            BigDecimal b2 = getScoreFromList(grades, 1);
            BigDecimal b3 = getScoreFromList(grades, 2);
            BigDecimal b4 = getScoreFromList(grades, 3);

            // Média aritmética simples
            BigDecimal avg = (b1.add(b2).add(b3).add(b4))
                    .divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);

            return new ReportCardGradeDTO(
                    tc.getSubject().getName(),
                    b1, b2, b3, b4, avg
            );
        }).collect(Collectors.toList());
    }

    // O método agora é privado pois é utilitário interno
    private boolean isStudentInRecovery(BigDecimal b1, BigDecimal b2, BigDecimal b3, BigDecimal b4) {
        BigDecimal sum = b1.add(b2).add(b3).add(b4);
        BigDecimal average = sum.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
        return average.compareTo(new BigDecimal("6.0")) < 0;
    }

    private void saveOrUpdateGrade(User studentUser, Assessment assessment, BigDecimal score,
                                   SingleStudentGradesRequest req, User teacher) {
        Grade grade = gradeRepository.findByStudentIdAndAssessmentId(assessment.getId(), studentUser.getId())
                .orElse(new Grade());

        grade.setStudent(studentUser);
        grade.setAssessment(assessment);
        grade.setScore(score);
        grade.setFeedback(req.feedback());
        // TODO: implementar os campos "isAbsent" e "isExcused" mais pra frente quando for refatorar!
        // grade.setAbsent(req.isAbsent() != null && req.isAbsent());
        // grade.setExcused(req.isExcused() != null && req.isExcused());

        // Agora o casting não falhará pois estamos passando o User já resolvido
        grade.setGradedBy(teacher);
        grade.setGradedAt(OffsetDateTime.now(ZoneOffset.UTC));

        gradeRepository.save(grade);
    }

    /**
     * Método auxiliar para extrair nota da lista de forma segura
     */
    private BigDecimal getScoreFromList(List<Grade> grades, int index) {
        if (grades != null && index < grades.size()) {
            BigDecimal score = grades.get(index).getScore();
            return score != null ? score : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private void saveOrUpdateGrade(User studentUser, Assessment assessment, BigDecimal score,
                                   String feedback, Boolean absent, Boolean excused, User teacher) {
        Grade grade = gradeRepository.findByStudentIdAndAssessmentId(assessment.getId(), studentUser.getId())
                .orElse(new Grade());

        grade.setStudent(studentUser);
        grade.setAssessment(assessment);
        grade.setScore(score);
        grade.setFeedback(feedback);
        grade.setAbsent(absent != null && absent);
        grade.setExcused(excused != null && excused);
        grade.setGradedBy(teacher); // Auditoria: Quem deu a nota
        grade.setGradedAt(OffsetDateTime.now(ZoneOffset.UTC));

        gradeRepository.save(grade);
    }

    /**
     * Implementação solicitada: Calcula a média global de todas as notas do aluno.
     */
    @Override
    @Transactional(readOnly = true)
    public Double calculateStudentGlobalAverage(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        BigDecimal average = gradeRepository.calculateWeightedAverage(student.getUser().getId());
        return average != null ? average.doubleValue() : 0.0;
    }

    /**
     * Método auxiliar para calcular a média parcial com precisão BigDecimal.
     * Útil se você decidir processar métricas antes de enviar ao Controller.
     */
    public BigDecimal calculatePartialAverage(BigDecimal g1, BigDecimal g2) {
        if (g1 == null || g2 == null) {
            return BigDecimal.ZERO;
        }
        return g1.add(g2)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    private ActiveClassResponse convertToDto(TeacherClass teacherClass) {
        ActiveClassResponse dto = new ActiveClassResponse();
        dto.setId(teacherClass.getId().toString());
        dto.setName(teacherClass.getSchoolClass().getName()); // Acessando a Turma vinculada
        dto.setSubject(teacherClass.getSubject().getName());  // Acessando a Disciplina vinculada

        // MOCK: studentCount adicionado ao backlog para implementação com DB real
        dto.setStudentCount(25);
        dto.setNextLesson("Segunda-feira, 08:00");

        return dto;
    }

    /**
     * Método auxiliar para verificar se o usuário tem role de professor
     */
    private boolean isUserTeacher(User user) {
        if (user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> {
                    String roleName = role.name().toUpperCase();
                    return roleName.equals("TEACHER") ||
                            roleName.equals("ROLE_TEACHER");
                });
    }
}