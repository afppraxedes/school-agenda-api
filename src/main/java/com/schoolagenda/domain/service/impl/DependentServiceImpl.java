package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.GradeStudentDTO;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.repository.GradeRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.service.DependentService;
import com.schoolagenda.domain.service.TeacherClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DependentServiceImpl implements DependentService {

    private final StudentRepository studentRepository;
    private final TeacherClassService teacherClassService;
    private final GradeRepository gradeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GradeStudentDTO> getDependentsPerformance(Long responsibleId) {
        // 1. Busca os alunos vinculados a este responsável
//        List<Student> dependents = studentRepository.findByResponsibleId(responsibleId);
        List<Student> dependents = studentRepository.findByResponsibleUserId(responsibleId);

        // 2. Para cada dependente, buscamos as médias e notas (reaproveitando a lógica sênior)
        return dependents.stream().map(student -> {
            // Buscamos as notas e calculamos a média global ponderada
            BigDecimal globalAvg = gradeRepository.calculateWeightedAverage(student.getUser().getId());

            // Retornamos o DTO que o Responsável usará no Dashboard
            return new GradeStudentDTO(
                    student.getId(),
                    student.getFullName(),
                    null, null, null, null, // Notas detalhadas podem ser buscadas em outro nível se necessário
                    globalAvg != null ? globalAvg : BigDecimal.ZERO,
                    "Acompanhe o desempenho acadêmico"
//                    false, false
            );
        }).toList();
    }
}