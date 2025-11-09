// src/main/java/com/schoolagenda/domain/service/TeacherClassServiceImpl.java
package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.domain.model.TeacherClass;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.TeacherClassRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherClassServiceImpl implements TeacherClassService {

    @Autowired
    private TeacherClassRepository teacherClassRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public TeacherClassResponse createTeacherClass(TeacherClassRequest request) {
        // Check if relationship already exists
        if (teacherClassRepository.existsByTeacherIdAndClassName(
                request.getTeacherId(), request.getClassName())) {
            throw new RuntimeException("Teacher already assigned to this class");
        }

        // Find teacher
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));

        // Verify user is a teacher
        if (!isUserTeacher(teacher)) {
            throw new RuntimeException("User is not a teacher");
        }

        // Create and save teacher-class relationship
        TeacherClass teacherClass = new TeacherClass(teacher, request.getClassName());
        TeacherClass savedTeacherClass = teacherClassRepository.save(teacherClass);

        return convertToResponse(savedTeacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherClassResponse getTeacherClassById(Long id) {
        TeacherClass teacherClass = teacherClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher class not found with id: " + id));

        return convertToResponse(teacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getClassesByTeacher(Long teacherId) {
        List<TeacherClass> teacherClasses = teacherClassRepository.findClassesWithTeacherByTeacherId(teacherId);

        return teacherClasses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getTeachersByClass(String className) {
        List<TeacherClass> teacherClasses = teacherClassRepository.findTeachersByClassName(className);

        return teacherClasses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherClassResponse getTeacherClass(Long teacherId, String className) {
        TeacherClass teacherClass = teacherClassRepository
                .findByTeacherIdAndClassName(teacherId, className)
                .orElseThrow(() -> new RuntimeException(
                        "Teacher class not found for teacher: " + teacherId + " and class: " + className));

        return convertToResponse(teacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean teacherClassExists(Long teacherId, String className) {
        return teacherClassRepository.existsByTeacherIdAndClassName(teacherId, className);
    }

    @Override
    @Transactional
    public TeacherClassResponse updateTeacherClass(Long id, TeacherClassRequest request) {
        TeacherClass teacherClass = teacherClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher class not found with id: " + id));

        // Check if new relationship would create a duplicate
        if (!teacherClass.getTeacher().getId().equals(request.getTeacherId()) ||
                !teacherClass.getClassName().equals(request.getClassName())) {

            if (teacherClassRepository.existsByTeacherIdAndClassName(
                    request.getTeacherId(), request.getClassName())) {
                throw new RuntimeException("Teacher already assigned to this class");
            }
        }

        // Update teacher if changed
        if (!teacherClass.getTeacher().getId().equals(request.getTeacherId())) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
            teacherClass.setTeacher(teacher);

            // CORREÇÃO: Verifica se o novo usuário é professor
            if (!isUserTeacher(teacher)) {
                throw new RuntimeException("User is not a teacher");
            }

            teacherClass.setTeacher(teacher);
        }

        // Update class name
        teacherClass.setClassName(request.getClassName());

        TeacherClass updatedTeacherClass = teacherClassRepository.save(teacherClass);
        return convertToResponse(updatedTeacherClass);
    }

    @Override
    @Transactional
    public void deleteTeacherClass(Long id) {
        if (!teacherClassRepository.existsById(id)) {
            throw new RuntimeException("Teacher class not found with id: " + id);
        }
        teacherClassRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteTeacherClass(Long teacherId, String className) {
        TeacherClass teacherClass = teacherClassRepository
                .findByTeacherIdAndClassName(teacherId, className)
                .orElseThrow(() -> new RuntimeException(
                        "Teacher class not found for teacher: " + teacherId + " and class: " + className));

        teacherClassRepository.delete(teacherClass);
    }

    @Override
    @Transactional(readOnly = true)
    public long getClassCountByTeacher(Long teacherId) {
        return teacherClassRepository.countByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDistinctClassNames() {
        return teacherClassRepository.findDistinctClassNames();
    }

    /**
     * Método auxiliar para verificar se o usuário tem role de professor
     * Considera diferentes formatos de roles: "TEACHER", "ROLE_TEACHER"
     */
    private boolean isUserTeacher(User user) {
        if (user.getRoles() == null /* || user.getRoles().isEmpty() */) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> {
                    String roleName = role.name().toUpperCase();
                    return roleName.equals("TEACHER") ||
                            roleName.equals("ROLE_TEACHER");
                });
    }

    /**
     * Método robusto para verificar roles de professor
     */
//    private boolean isUserTeacher(User user) {
//        if (user.getRoles() == null) {
//            return false;
//        }
//
//        return user.getRoles().stream()
//                .map(role -> role.name().toUpperCase())
//                .anyMatch(roleName ->
//                        roleName.equals("TEACHER") ||
//                                roleName.equals("ROLE_TEACHER") ||
//                                roleName.equals("PROFESSOR") || // Caso tenha em português
//                                roleName.endsWith("_TEACHER") ||
//                                roleName.startsWith("TEACHER_")
//                );
//    }

    /**
     * Converts TeacherClass entity to Response DTO
     */
    private TeacherClassResponse convertToResponse(TeacherClass teacherClass) {
        return new TeacherClassResponse(
                teacherClass.getId(),
                teacherClass.getTeacher().getId(),
                teacherClass.getTeacher().getName(),
                teacherClass.getTeacher().getEmail(),
                teacherClass.getClassName(),
                teacherClass.getCreatedAt(),
                teacherClass.getUpdatedAt()
        );
    }
}