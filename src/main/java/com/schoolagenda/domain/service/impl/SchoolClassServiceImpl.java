package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.SchoolClassRequest;
import com.schoolagenda.application.web.dto.response.SchoolClassResponse;
import com.schoolagenda.application.web.mapper.SchoolClassMapper;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.SchoolClass;
import com.schoolagenda.domain.repository.SchoolClassRepository;

import com.schoolagenda.domain.service.SchoolClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// SchoolClassService.java (Implementação)
@Service
@RequiredArgsConstructor
public class SchoolClassServiceImpl implements SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final SchoolClassMapper schoolClassMapper;

    @Override
    @Transactional
    public SchoolClassResponse create(SchoolClassRequest request) {
        if (schoolClassRepository.existsByName(request.name())) {
            throw new BusinessResourceException("Já existe uma turma com o nome: " + request.name());
        }

        SchoolClass schoolClass = schoolClassMapper.toEntity(request);
        SchoolClass savedClass = schoolClassRepository.save(schoolClass);

        return schoolClassMapper.toResponse(savedClass);
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolClassResponse findById(Long id) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));
        return schoolClassMapper.toResponse(schoolClass);
    }

    @Transactional(readOnly = true)
    public List<SchoolClassResponse> findAll() {
        return schoolClassRepository.findAll().stream()
                .map(schoolClassMapper::toResponse)
                .toList();
    }

    @Transactional
    public SchoolClassResponse update(Long id, SchoolClassRequest request) {
        if (schoolClassRepository.existsByName(request.name())) {
            throw new BusinessResourceException("Já existe uma turma com o nome: "  + request.name());
        }

        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID:" + id));

        schoolClassMapper.updateEntity(schoolClass, request);
        SchoolClass updateSchoolClass = schoolClassRepository.save(schoolClass);

        return schoolClassMapper.toResponse(updateSchoolClass);
    }

    @Transactional
    public void delete(Long id) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));

        schoolClassRepository.deleteById(schoolClass.getId());
    }
}
