package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.AttendanceBulkRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.application.web.mapper.AttendanceMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Attendance;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.repository.AttendanceRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.SubjectRepository;
import com.schoolagenda.domain.repository.TeacherClassRepository;
import com.schoolagenda.domain.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherClassRepository teacherClassRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public void saveAll(AttendanceBulkRequest request, AgendaUserDetails currentUser) {
        log.info("Registrando frequência em lote para a Turma: {}, Disciplina: {}, Data: {}",
                request.schoolClassId(), request.subjectId(), request.date());

        // 1. SEGURANÇA: Validar se o Professor tem permissão para esta Turma e Disciplina
        if (currentUser.hasRole(UserRole.TEACHER)) {
            boolean isAuthorized = teacherClassRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
                    currentUser.getId(), request.subjectId(), request.schoolClassId());

            if (!isAuthorized) {
                throw new AccessDeniedException("Você não tem permissão para registrar frequência nesta turma/disciplina.");
            }
        }

        // 2. Buscar Entidades de Referência
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        // 3. Processar cada presença da lista
        List<Attendance> attendanceList = request.attendances().stream().map(item -> {
            Student student = studentRepository.findById(item.studentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado: " + item.studentId()));

            // Tenta buscar registro existente para atualizar (evitar duplicidade) ou cria novo
            Attendance attendance = attendanceRepository
                    .findByStudentIdAndSubjectIdAndDate(student.getId(), subject.getId(), request.date())
                    .orElse(new Attendance());

            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setDate(request.date());
            attendance.setPresent(item.present());
            attendance.setNote(item.note());

            return attendance;
        }).toList();

        attendanceRepository.saveAll(attendanceList);
        log.info("Total de {} registros de frequência processados.", attendanceList.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttendanceResponse> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date) {
        return attendanceRepository.findByStudentIdAndSubjectIdAndDate(studentId, subjectId, date)
                .map(attendanceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStudentIdAndSubjectIdAndPresentFalse(Long studentId, Long subjectId) {
        return attendanceRepository.countByStudentIdAndSubjectIdAndPresentFalse(studentId, subjectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStudentIdAndSubjectId(Long studentId, Long subjectId) {
        return attendanceRepository.countByStudentIdAndSubjectId(studentId, subjectId);
    }
}
