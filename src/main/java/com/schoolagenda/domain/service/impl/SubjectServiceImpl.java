package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.subject.SubjectFilterRequest;
import com.schoolagenda.application.web.dto.request.SubjectRequest;
import com.schoolagenda.application.web.dto.response.SubjectResponse;
import com.schoolagenda.domain.exception.InvalidFilterException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Subject;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.SubjectRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.SubjectService;
import com.schoolagenda.application.web.mapper.SubjectMapper;
import com.schoolagenda.domain.specification.SubjectSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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
        log.info("Creating a new subject: {}", request.getName());

        validateSubjectRequest(request);

        Subject subject = subjectMapper.toEntity(request);

        if (request.getTeacherUserId() != null) {
            User teacher = userRepository.findById(request.getTeacherUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + request.getTeacherUserId()));
            subject.setTeacher(teacher);
        }

        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject created with ID: {}", savedSubject.getId());

        return subjectMapper.toResponse(savedSubject);
    }

    @Transactional(readOnly = true)
    public SubjectResponse findById(Long id) {
        log.debug("Searching for subject with ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        return subjectMapper.toResponse(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findAll() {
        log.debug("Searching for all subjects");
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findByTeacher(Long teacherId) {
        log.debug("Searching for subjects by teacher ID.: {}", teacherId);

        List<Subject> response = subjectRepository.findByTeacherId(teacherId);

        // TODO: esta validação está apenas como "provisória". Criar um método que faça
        // a busca, para que não precise ficar "replicando" esta mesma instrução!
        if(response.isEmpty()) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + teacherId);
        }

        return response.stream()
                .map(subjectMapper::toResponse)
                .toList();
//        return subjectRepository.findByTeacherId(teacherId).stream()
//                .map(subjectMapper::toResponse)
//                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findBySchoolYear(String schoolYear) {
        log.debug("BSearching for subjects in the academic year: {}", schoolYear);

        List<Subject> response = subjectRepository.findBySchoolYear(schoolYear);

        if(response.isEmpty()) {
            throw new ResourceNotFoundException("No subjects found for the academic year " + schoolYear);
        }

        return response.stream()
                .map(subjectMapper::toResponse)
                .toList();
//        return subjectRepository.findBySchoolYear(schoolYear).stream()
//                .map(subjectMapper::toResponse)
//                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findActive() {
        log.debug("Searching for active subjects");
        return subjectRepository.findByActiveTrue().stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        log.info("Updating subject ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        validateSubjectRequest(request, id);

        subjectMapper.updateEntity(request, subject);

        if (request.getTeacherUserId() != null) {
            User teacher = userRepository.findById(request.getTeacherUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + request.getTeacherUserId()));
            subject.setTeacher(teacher);
        } else {
            subject.setTeacher(null);
        }

        Subject updatedSubject = subjectRepository.save(subject);
        log.info("Subject updated with ID: {}", id);

        return subjectMapper.toResponse(updatedSubject);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting subject ID: {}", id);

        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }

        subjectRepository.deleteById(id);
        log.info("Subject deleted with ID: {}", id);
    }

    @Transactional
    public SubjectResponse toggleStatus(Long id) {
        log.info("Changing subject ID status: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        subject.setActive(!subject.getActive());
        Subject updatedSubject = subjectRepository.save(subject);

        log.info("Subject ID status: {} changed to: {}", id, updatedSubject.getActive());
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
                        String.format("There is already a subject named Mathematics '%s' in the academic year '%s'",
                                request.getName(), request.getSchoolYear())
                );
            }
        }
    }

    // Paginação
    // TODO: verificar essa "estrutura" (arquivos e respectivos pacotes) para paginação, pois se trata de consultas
    // mais complexas e acho que deveriam ficar numa "outra estrutura" (pacotes) para esta finalizadade!
    @Transactional(readOnly = true)
    public PaginationResponse<SubjectResponse> search(SubjectFilterRequest filter, PaginationRequest pageRequest) {
        // Validação do filter
        if (filter != null && filter.hasName() && filter.getName().length() < 2) {
            throw new InvalidFilterException("Termo de busca deve ter pelo menos 2 caracteres");
        }

        Specification<Subject> spec = SubjectSpecifications.withFilters(filter);
        Page<Subject> page = subjectRepository.findAll(spec, pageRequest.toPageable());

        return PaginationResponse.of(page.map(subjectMapper::toResponse));
    }
}
