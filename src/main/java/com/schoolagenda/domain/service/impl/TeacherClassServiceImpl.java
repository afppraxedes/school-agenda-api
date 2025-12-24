package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.application.web.mapper.TeacherClassMapper;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.SchoolClass;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.TeacherClass;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.SchoolClassRepository;
import com.schoolagenda.domain.repository.SubjectRepository;
import com.schoolagenda.domain.repository.TeacherClassRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherClassServiceImpl implements TeacherClassService {

    @Autowired
    private TeacherClassRepository teacherClassRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private TeacherClassMapper teacherClassMapper;

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