package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.TimetableRepository;
import com.schoolagenda.domain.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final TimetableRepository timetableRepository;
    private final StudentRepository studentRepository;

    public ScheduleServiceImpl(TimetableRepository timetableRepository, StudentRepository studentRepository) {
        this.timetableRepository = timetableRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<TimetableResponse> findTodayByStudent(Long studentId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        // No mundo real usaríamos LocalDate.now().getDayOfWeek()
        // Para o seu teste de hoje (Domingo), você pode forçar MONDAY para validar
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        return timetableRepository.findByClassIdAndDayOfWeek(student.getSchoolClass().getId(), today)
                .stream()
                .map(t -> {
                    var tc = t.getTeacherClass();
                    return new TimetableResponse(
                            tc.getId(),
                            tc.getTeacher().getId(),
                            tc.getTeacher().getName(),    // Busca via TeacherClass
                            tc.getSubject().getName(),    // Busca via TeacherClass
                            student.getSchoolClass().getName(),
                            t.getDayOfWeek(),
                            t.getStartTime(),
                            t.getEndTime(),
                            t.getRoomName()
                    );
                })
                .toList();
    }

}
