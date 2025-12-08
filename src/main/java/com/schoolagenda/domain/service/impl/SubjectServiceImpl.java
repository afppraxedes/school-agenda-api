package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.SubjectRequest;
import com.schoolagenda.application.web.dto.response.SubjectResponse;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.SubjectRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.SubjectService;
import com.schoolagenda.application.web.mapper.SubjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final SubjectMapper subjectMapper;

    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        log.info("Criando nova disciplina: {}", request.getName());

        validateSubjectRequest(request);

        Subject subject = subjectMapper.toEntity(request);

        if (request.getTeacherUserId() != null) {
            User teacher = userRepository.findById(request.getTeacherUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + request.getTeacherUserId()));
            subject.setTeacher(teacher);
        }

        Subject savedSubject = subjectRepository.save(subject);
        log.info("Disciplina criada com ID: {}", savedSubject.getId());

        return subjectMapper.toResponse(savedSubject);
    }

    @Transactional(readOnly = true)
    public SubjectResponse findById(Long id) {
        log.debug("Buscando disciplina com ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + id));

        return subjectMapper.toResponse(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findAll() {
        log.debug("Buscando todas as disciplinas");
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findByTeacher(Long teacherId) {
        log.debug("Buscando disciplinas do professor ID: {}", teacherId);
        return subjectRepository.findByTeacherId(teacherId).stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findBySchoolYear(String schoolYear) {
        log.debug("Buscando disciplinas do ano letivo: {}", schoolYear);
        return subjectRepository.findBySchoolYear(schoolYear).stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findActive() {
        log.debug("Buscando disciplinas ativas");
        return subjectRepository.findByActiveTrue().stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        log.info("Atualizando disciplina ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + id));

        validateSubjectRequest(request, id);

        subjectMapper.updateEntity(request, subject);

        if (request.getTeacherUserId() != null) {
            User teacher = userRepository.findById(request.getTeacherUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + request.getTeacherUserId()));
            subject.setTeacher(teacher);
        } else {
            subject.setTeacher(null);
        }

        Subject updatedSubject = subjectRepository.save(subject);
        log.info("Disciplina atualizada com ID: {}", id);

        return subjectMapper.toResponse(updatedSubject);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Excluindo disciplina ID: {}", id);

        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disciplina não encontrada com ID: " + id);
        }

        subjectRepository.deleteById(id);
        log.info("Disciplina excluída com ID: {}", id);
    }

    @Transactional
    public SubjectResponse toggleStatus(Long id) {
        log.info("Alternando status da disciplina ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com ID: " + id));

        subject.setActive(!subject.getActive());
        Subject updatedSubject = subjectRepository.save(subject);

        log.info("Status da disciplina ID: {} alterado para: {}", id, updatedSubject.getActive());
        return subjectMapper.toResponse(updatedSubject);
    }

    private void validateSubjectRequest(SubjectRequest request) {
        validateSubjectRequest(request, null);
    }

    private void validateSubjectRequest(SubjectRequest request, Long excludeId) {
        if (request.getName() != null && request.getSchoolYear() != null) {
            boolean exists;
            if (excludeId != null) {
                exists = subjectRepository.findByFilters(
                                request.getName(),
                                request.getSchoolYear(),
                                null)
                        .stream()
                        .anyMatch(s -> !s.getId().equals(excludeId));
            } else {
                exists = subjectRepository.existsByNameAndSchoolYear(
                        request.getName(),
                        request.getSchoolYear()
                );
            }

            if (exists) {
                throw new IllegalArgumentException(
                        String.format("Já existe uma disciplina com nome '%s' no ano letivo '%s'",
                                request.getName(), request.getSchoolYear())
                );
            }
        }
    }
}
