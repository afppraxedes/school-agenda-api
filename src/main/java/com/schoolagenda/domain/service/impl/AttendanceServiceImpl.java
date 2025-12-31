package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.AttendanceBulkRequest;
import com.schoolagenda.application.web.dto.response.AttendanceResponse;
import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.application.web.mapper.AttendanceMapper;
import com.schoolagenda.application.web.mapper.TimetableMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Attendance;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.Timetable;
import com.schoolagenda.domain.repository.*;
import com.schoolagenda.domain.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TimetableRepository timetableRepository;
    private final TeacherClassRepository teacherClassRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceMapper attendanceMapper;
    private final TimetableMapper timetableMapper;

    @Override
    @Transactional
    public void saveBulk(AttendanceBulkRequest request, Long teacherUserId) {
        // 1. Busca o horário
        Timetable timetable = timetableRepository.findById(request.timetableId())
                .orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado"));

        // 2. Valida o professor (Segurança)
        if (!timetable.getTeacherClass().getTeacher().getId().equals(teacherUserId)) {
            throw new BusinessException("Você não tem permissão para esta turma.");
        }

        // 3. Processa a lista
        List<Attendance> attendances = request.students().stream().map(sReq -> {
            // Busca existente para evitar duplicados na mesma aula/data
            Attendance attendance = attendanceRepository
                    .findByStudentIdAndTimetableIdAndDate(sReq.studentId(), request.timetableId(), request.date())
                    .orElse(new Attendance());

            attendance.setStudent(studentRepository.getReferenceById(sReq.studentId()));
            attendance.setSubject(timetable.getTeacherClass().getSubject());
            attendance.setTimetable(timetable);
            attendance.setDate(request.date());
            attendance.setPresent(sReq.present());
            attendance.setNote(sReq.note());

            return attendance;
        }).toList();

        attendanceRepository.saveAll(attendances);
    }

    // LOTE DE FREQUÊNCIAS: Método anterior comentado para revisão futura
//    @Override
//    @Transactional
//    public void saveAll(AttendanceBulkRequest request, AgendaUserDetails currentUser) {
//        log.info("Registrando frequência em lote para a Turma: {}, Disciplina: {}, Data: {}",
//                request.schoolClassId(), request.subjectId(), request.date());
//
//        // 1. SEGURANÇA: Validar se o Professor tem permissão para esta Turma e Disciplina
//        if (currentUser.hasRole(UserRole.TEACHER)) {
//            boolean isAuthorized = teacherClassRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
//                    currentUser.getId(), request.subjectId(), request.schoolClassId());
//
//            if (!isAuthorized) {
//                throw new AccessDeniedException("Você não tem permissão para registrar frequência nesta turma/disciplina.");
//            }
//        }
//
//        // 2. Buscar Entidades de Referência
//        Subject subject = subjectRepository.findById(request.subjectId())
//                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));
//
//        // 3. Processar cada presença da lista
//        List<Attendance> attendanceList = request.attendances().stream().map(item -> {
//            Student student = studentRepository.findById(item.studentId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado: " + item.studentId()));
//
//            // Tenta buscar registro existente para atualizar (evitar duplicidade) ou cria novo
//            Attendance attendance = attendanceRepository
//                    .findByStudentIdAndSubjectIdAndDate(student.getId(), subject.getId(), request.date())
//                    .orElse(new Attendance());
//
//            attendance.setStudent(student);
//            attendance.setSubject(subject);
//            attendance.setDate(request.date());
//            attendance.setPresent(item.present());
//            attendance.setNote(item.note());
//
//            return attendance;
//        }).toList();
//
//        attendanceRepository.saveAll(attendanceList);
//        log.info("Total de {} registros de frequência processados.", attendanceList.size());
//    }

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

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentHistory(Long studentId, LocalDate start, LocalDate end) {
        return attendanceRepository.findAttendanceHistory(studentId, start, end)
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalAbsences(Long studentId, Long subjectId) {
        return attendanceRepository.countByStudentIdAndSubjectIdAndPresentFalse(studentId, subjectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalClasses(Long studentId, Long subjectId) {
        return attendanceRepository.countByStudentIdAndSubjectId(studentId, subjectId);
    }
}
