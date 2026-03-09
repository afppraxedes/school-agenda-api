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
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.*;
import com.schoolagenda.domain.repository.*;
import com.schoolagenda.domain.service.PushSubscriptionService;
import com.schoolagenda.domain.service.TeacherClassService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<Student> students = studentRepository.findBySchoolClassId(tc.getSchoolClass().getId());

        return students.stream().map(s -> {
            // 2. Buscamos as notas que já existem para este aluno nesta disciplina
            List<Grade> grades = gradeRepository.findTop4ByStudentIdAndSubjectId(s.getUser().getId(), tc.getSubject().getId());

            // 3. Mapeamento SEGURO: Se a lista for menor que 4, usamos 0.0
            // Isso evita o erro de IndexOutOfBounds e permite o primeiro lançamento
            BigDecimal g1 = getScoreFromList(grades, 0);
            BigDecimal g2 = getScoreFromList(grades, 1);
            BigDecimal g3 = getScoreFromList(grades, 2);
            BigDecimal g4 = getScoreFromList(grades, 3);

            // 4. Média Ponderada ou Aritmética
            BigDecimal avg = (g1.add(g2).add(g3).add(g4)).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);

            return new GradeStudentDTO(
                    s.getId(),
                    s.getFullName(),
                    g1, g2, g3, g4,
                    avg,
                    grades.isEmpty() ? "" : grades.get(0).getFeedback()
//                    grades.isEmpty() ? false : grades.get(0).getAbsent(),
//                    grades.isEmpty() ? false : grades.get(0).getExcused()
            );
        }).toList();
    }
//    @Override
//    @Transactional(readOnly = true)
//    public List<GradeStudentDTO> getStudentsGrades(Long teacherClassId) {
//        TeacherClass tc = teacherClassRepository.findById(teacherClassId)
//                .orElseThrow(() -> new ResourceNotFoundException("Vínculo não encontrado"));
//
//        List<Student> students = studentRepository.findBySchoolClassId(tc.getSchoolClass().getId());
//
//        return students.stream().map(s -> {
//            // Buscamos as notas dos 4 bimestres vinculadas ao User do aluno e ao Subject da turma
//            List<Grade> grades = gradeRepository.findTop4ByStudentIdAndSubjectId(s.getUser().getId(), tc.getSubject().getId());
//
//            // Mapeamento seguro para evitar IndexOutOfBounds
//            BigDecimal g1 = grades.size() > 0 ? grades.get(0).getScore() : BigDecimal.ZERO;
//            BigDecimal g2 = grades.size() > 1 ? grades.get(1).getScore() : BigDecimal.ZERO;
//            BigDecimal g3 = grades.size() > 2 ? grades.get(2).getScore() : BigDecimal.ZERO;
//            BigDecimal g4 = grades.size() > 3 ? grades.get(3).getScore() : BigDecimal.ZERO;
//
//            // Pegamos o feedback e status da nota mais recente lançada, se existir
//            String feedback = grades.isEmpty() ? "" : grades.get(0).getFeedback();
//            Boolean absent = grades.isEmpty() ? false : grades.get(0).getAbsent();
//            Boolean excused = grades.isEmpty() ? false : grades.get(0).getExcused();
//
//            // Média Aritmética Simples para os 4 bimestres
//            BigDecimal avg = (g1.add(g2).add(g3).add(g4))
//                    .divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
//
//            // Retorna o DTO alinhado ao novo Record de 10 campos
//            return new GradeStudentDTO(
//                    s.getId(),
//                    s.getFullName(),
//                    g1, g2, g3, g4,
//                    avg,
//                    feedback,
//                    absent,
//                    excused
//            );
//        }).toList();
//    }
//    @Override
//    @Transactional(readOnly = true)
//    public List<GradeStudentDTO> getStudentsGrades(Long teacherClassId) {
//        // 1. Busca a TeacherClass para saber qual a SchoolClass e Subject
//        TeacherClass tc = teacherClassRepository.findById(teacherClassId)
//                .orElseThrow(() -> new ResourceNotFoundException("Vínculo não encontrado"));
//
//        // 2. Busca todos os estudantes daquela turma
//            List<Student> students = studentRepository.findBySchoolClassId(tc.getSchoolClass().getId());
//
//        // 3. Mapeia para o DTO (Buscando notas existentes no banco)
//        return students.stream().map(s -> {
//            // Aqui buscamos as notas das duas primeiras avaliações daquela disciplina/turma
//            List<Grade> grades = gradeRepository.findTop2ByStudentIdAndSubjectId(s.getUser().getId(), tc.getSubject().getId());
//
//            BigDecimal g1 = grades.size() > 0 ? grades.get(0).getScore() : BigDecimal.ZERO;
//            BigDecimal g2 = grades.size() > 1 ? grades.get(1).getScore() : BigDecimal.ZERO;
//            BigDecimal avg = (g1.add(g2)).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
//
//            return new GradeStudentDTO(s.getId(), s.getUser().getId(), s.getFullName(), g1, g2, avg);
//        }).toList();
//    }

//    @Override
//    @Transactional
//    public void saveAllGrades(List<SaveGradeRequest> grades) {
//        for (SaveGradeRequest req : grades) {
//            // Converte o item da lista para o formato de processamento individual
//            SingleStudentGradesRequest singleReq = new SingleStudentGradesRequest(
//                    req.studentId(), req.grade1(), req.grade2(), req.teacherClassId()
//            );
//            this.updateStudentGrades(singleReq);
//        }
//    }

    @Override
    @Transactional
    public void saveAllGrades(List<SaveGradeRequest> grades) {
        for (SaveGradeRequest req : grades) {
            // Mapeia a lista recebida para o processamento individual que contém a regra de negócio
            SingleStudentGradesRequest singleReq = new SingleStudentGradesRequest(
                    req.studentId(),
                    req.teacherClassId(),
                    req.grade1(), req.grade2(), req.grade3(), req.grade4(),
                    req.feedback()
//                    req.isAbsent(),
//                    req.isExcused()
            );
            this.updateStudentGrades(singleReq);
        }
    }

//    @Override
//    public void updateGrades(List<SaveGradeRequest> grades) {
//        for (SaveGradeRequest request : grades) {
//            studentRepository.findById(request.studentId()).ifPresent(student -> {
//                // 1. Atualizamos as notas individuais (Se você tiver esses campos na entidade)
//                // Caso use uma tabela separada de 'Grades', injete o repositório dela aqui.
//
//                // 2. Cálculo da Média (Exemplo simples: Aritmética)
//                double g1 = request.grade1() != null ? request.grade1() : 0.0;
//                double g2 = request.grade2() != null ? request.grade2() : 0.0;
//                double average = (g1 + g2) / 2;
//
//                // 3. Atualizamos a média global do estudante
//                student.setGlobalAverage(average);
//
//                // Registro de auditoria simples
//                logger.info("📈 Nota atualizada: Aluno ID {}, Nova Média: {}", student.getId(), average);
//
//                studentRepository.save(student);
//            });
//        }
//    }

    @Override
    @Transactional
    public void saveAllStudentGrades(List<SingleStudentGradesRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            logger.warn("⚠️ Lista de notas recebida está vazia.");
            return;
        }

        // O loop garante que a regra de negócio individual seja aplicada a cada aluno
        for (SingleStudentGradesRequest req : requests) {
            try {
                // Reaproveita a lógica: Busca assessments, salva Grades, calcula média e dispara Push
                this.updateStudentGrades(req);
            } catch (Exception e) {
                logger.error("❌ Erro ao processar notas do aluno ID {}: {}", req.studentId(), e.getMessage());
                // Lançamos a exceção para dar rollback em toda a operação de lote se um falhar
                throw e;
            }
        }
        logger.info("✅ Processamento de lote finalizado para {} registros.", requests.size());
    }

    @Override
    @Transactional
    public void updateStudentGrades(SingleStudentGradesRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        // 1. Localiza as 4 avaliações da turma (B1, B2, B3, B4)
        List<Assessment> assessments = assessmentRepository.findByTeacherClassIdOrderByDueDateAsc(request.teacherClassId());

        // Verificação de segurança: O Seed SQL deve ter criado 4 registros
        if (assessments.size() < 4) {
            throw new IllegalStateException("Configuração incompleta: A turma deve ter 4 bimestres cadastrados.");
        }

        // CORREÇÃO: Pegar o ID do professor logado através do Details
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentTeacherId;

        if (principal instanceof com.schoolagenda.application.web.security.dto.AgendaUserDetails userDetails) {
            currentTeacherId = userDetails.getId();
        } else {
            throw new IllegalStateException("Usuário logado não possui um perfil AgendaUserDetails válido.");
        }

        // Buscamos a referência da entidade User (professor) para auditoria
        User teacherEntity = userRepository.getReferenceById(currentTeacherId);

        // 2. Persistência das Notas (Passando a entidade correta)
        saveOrUpdateGrade(student.getUser(), assessments.get(0), request.grade1(), request, teacherEntity);
        saveOrUpdateGrade(student.getUser(), assessments.get(1), request.grade2(), request, teacherEntity);
        saveOrUpdateGrade(student.getUser(), assessments.get(2), request.grade3(), request, teacherEntity);
        saveOrUpdateGrade(student.getUser(), assessments.get(3), request.grade4(), request, teacherEntity);

//        // 3. Recálculo da Média Global via Repository (JPQL Ponderada)
//        BigDecimal newAvg = gradeRepository.calculateWeightedAverage(student.getUser().getId());
//        student.setGlobalAverage(newAvg != null ? newAvg : BigDecimal.ZERO);
//        studentRepository.save(student);

        // 4. WebPush: Notifica o Aluno do novo status
//        pushService.sendPushToUser(student.getUser().getId(), "Notas Atualizadas", "Sua média global é: " + student.getGlobalAverage());

        // USO DA REFERÊNCIA: Verificamos a recuperação após o cálculo da média
        BigDecimal newAvg = gradeRepository.calculateWeightedAverage(student.getUser().getId());
        BigDecimal finalAvg = newAvg != null ? newAvg : BigDecimal.ZERO;

        // Se a média for < 6.0, podemos setar um status ou enviar um alerta específico no Push
        boolean recoveryNeeded = isStudentInRecovery(
                request.grade1(), request.grade2(), request.grade3(), request.grade4()
        );

        student.setGlobalAverage(finalAvg);
        // student.setStatus(recoveryNeeded ? "RECUPERACAO" : "APROVADO"); // Se houver o campo
        studentRepository.save(student);

        String msg = recoveryNeeded
                ? "Sua média é " + finalAvg + ". Você está em recuperação."
                : "Parabéns! Sua média é " + finalAvg;

        pushService.sendPushToUser(student.getUser().getId(), "Boletim Atualizado", msg);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public PerformanceHistoryResponse getStudentHistory(Long studentId) {
//        // Busca as notas dos 4 bimestres ordenadas por data
//        List<Grade> grades = gradeRepository.findTop4ByStudentIdOrderByDate(studentId);
//
//        List<String> labels = List.of("B1", "B2", "B3", "B4");
//        List<BigDecimal> values = grades.stream()
//                .map(g -> g.getScore() != null ? g.getScore() : BigDecimal.ZERO)
//                .collect(Collectors.toList());
//
//        // Garante que a lista tenha 4 valores para o gráfico não quebrar
//        while (values.size() < 4) values.add(BigDecimal.ZERO);
//
//        return new PerformanceHistoryResponse(labels, values);
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public PerformanceHistoryResponse getStudentHistory(Long studentId) {
//        List<Grade> grades = gradeRepository.findAllByStudentIdOrderByDate(studentId);
//
//        List<String> labels = List.of("B1", "B2", "B3", "B4");
//        // Mapeia os valores das notas para o gráfico (B1, B2, B3, B4)
//        List<BigDecimal> values = grades.stream()
//                .map(g -> g.getScore() != null ? g.getScore() : BigDecimal.ZERO)
//                .limit(4) // Garante apenas os 4 bimestres
//                .collect(Collectors.toList());
//
//        while (values.size() < 4) values.add(BigDecimal.ZERO);
//
//        return new PerformanceHistoryResponse(labels, values);
//    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceHistoryResponse getStudentHistory(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        List<Grade> grades = gradeRepository.findAllByStudentIdOrderByDate(studentId);

        List<BigDecimal> values = grades.stream()
                .map(g -> g.getScore() != null ? g.getScore() : BigDecimal.ZERO)
                .limit(4)
                .collect(Collectors.toList());

        while (values.size() < 4) values.add(BigDecimal.ZERO);

        // Lógica: Recuperação se a média for inferior a 6.0
        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean recovery = sum.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP)
                .compareTo(new BigDecimal("6.0")) < 0;

        return new PerformanceHistoryResponse(
                student.getFullName(),
                List.of("B1", "B2", "B3", "B4"),
                values,
                recovery
        );
    }

//    @Override
//    public byte[] generateReportCardPDF(Long studentId) {
//        Student student = studentRepository.findById(studentId).orElseThrow();
//        // Lógica para desenhar o PDF com as notas e status de recuperação...
//        // Retorna o fluxo de bytes do PDF consolidado.
//        return new byte[0]; // Placeholder para sua biblioteca de PDF (iText/Jasper)
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ReportCardGradeDTO> listGradesByStudent(Long studentId) {
//        Student student = studentRepository.findById(studentId).orElseThrow();
//
//        // Busca todas as disciplinas da turma do aluno
//        List<TeacherClass> teacherClasses = teacherClassRepository.findBySchoolClassId(student.getSchoolClass().getId());
//
//        return teacherClasses.stream().map(tc -> {
//            List<Grade> grades = gradeRepository.findTop4ByStudentIdAndSubjectId(student.getUser().getId(), tc.getSubject().getId());
//
//            BigDecimal b1 = getScoreFromList(grades, 0);
//            BigDecimal b2 = getScoreFromList(grades, 1);
//            BigDecimal b3 = getScoreFromList(grades, 2);
//            BigDecimal b4 = getScoreFromList(grades, 3);
//
//            BigDecimal avg = (b1.add(b2).add(b3).add(b4)).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
//
//            return new ReportCardGradeDTO(
//                    tc.getSubject().getName(),
//                    b1, b2, b3, b4, avg
//            );
//        }).toList();
//    }

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
        Grade grade = gradeRepository.findByAssessmentIdAndStudentId(assessment.getId(), studentUser.getId())
                .orElse(new Grade());

        grade.setStudent(studentUser);
        grade.setAssessment(assessment);
        grade.setScore(score);
        grade.setFeedback(req.feedback());
//        grade.setAbsent(req.isAbsent() != null && req.isAbsent());
//        grade.setExcused(req.isExcused() != null && req.isExcused());

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
        Grade grade = gradeRepository.findByAssessmentIdAndStudentId(assessment.getId(), studentUser.getId())
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

//    @Override
//    @Transactional
//    public void updateStudentGrades(SingleStudentGradesRequest request) {
//        Student student = studentRepository.findById(request.studentId())
//                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));
//
//        // 1. Localiza as 2 avaliações padrão da disciplina/turma
//        List<Assessment> assessments = assessmentRepository.findByTeacherClassIdOrderByDueDateAsc(request.teacherClassId());
//
//        if (assessments.size() < 2) {
//            throw new IllegalStateException("A disciplina exige pelo menos 2 avaliações cadastradas.");
//        }
//
//        // 2. Persistência das Notas Individuais
//        saveOrUpdateGrade(student.getUser(), assessments.get(0), request.grade1());
//        saveOrUpdateGrade(student.getUser(), assessments.get(1), request.grade2());
//
//        // 3. Recálculo da Média Ponderada (Query JPQL que usa o 'weight' do Assessment)
//        BigDecimal newGlobalAverage = gradeRepository.calculateWeightedAverage(student.getUser().getId());
//
//        // 4. Atualização do "Cache" de média na entidade Student
//        student.setGlobalAverage(newGlobalAverage != null ? newGlobalAverage : BigDecimal.ZERO);
//        studentRepository.save(student);
//
//        // 5. Disparo do WebPush (O SW do Aluno interceptará isso para atualizar a UI sem F5)
//        pushService.sendPushToUser(
//                student.getUser().getId(),
//                "Nota Lançada!",
//                "Sua média global foi atualizada para " + student.getGlobalAverage()
//        );
//    }

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

//    private void saveOrUpdateGrade(User studentUser, Assessment assessment, BigDecimal score) {
//        // Busca nota existente ou cria nova
//        Grade grade = gradeRepository.findByAssessmentIdAndStudentId(assessment.getId(), studentUser.getId())
//                .orElse(new Grade());
//
//        grade.setStudent(studentUser);
//        grade.setAssessment(assessment);
//        grade.setScore(score);
//        grade.setMaxScore(assessment.getMaxScore()); // Garante o teto da nota
//        gradeRepository.save(grade);
//    }

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