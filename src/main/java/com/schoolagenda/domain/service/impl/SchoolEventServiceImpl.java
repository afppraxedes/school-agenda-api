package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.SchoolEventRequest;
import com.schoolagenda.application.web.dto.response.SchoolEventResponse;
import com.schoolagenda.application.web.mapper.SchoolEventMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.SchoolEvent;
import com.schoolagenda.domain.repository.SchoolClassRepository;
import com.schoolagenda.domain.repository.SchoolEventRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.service.SchoolEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolEventServiceImpl implements SchoolEventService {

    private final SchoolEventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolEventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SchoolEventResponse> getCalendar(OffsetDateTime start, OffsetDateTime end, AgendaUserDetails user) {
        if (user.hasRole(UserRole.ADMINISTRATOR) || user.hasRole(UserRole.DIRECTOR) /*"ADMINISTRATOR", "DIRECTOR"*/) {
            return eventRepository.findAllByStartDateBetweenOrderByStartDateAsc(start, end)
                    .stream().map(eventMapper::toResponse).toList();
        }

        Long classId = null;
        if (user.hasRole(UserRole.STUDENT) || user.hasRole(UserRole.RESPONSIBLE)) {
            classId = studentRepository.findClassIdByUserId(user.getId())
                    .orElse(null); // Se não tiver turma, verá apenas globais
        }

        return eventRepository.findByClassAndDateRange(classId, start, end)
                .stream().map(eventMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public SchoolEventResponse create(SchoolEventRequest request) {
        SchoolEvent event = eventMapper.toEntity(request);
        if (request.schoolClassId() != null) {
            event.setSchoolClass(schoolClassRepository.getReferenceById(request.schoolClassId()));
        }
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public SchoolEventResponse update(Long id, SchoolEventRequest request) {
        SchoolEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        eventMapper.updateEntity(request, event);

        if (request.schoolClassId() != null) {
            event.setSchoolClass(schoolClassRepository.getReferenceById(request.schoolClassId()));
        } else {
            event.setSchoolClass(null);
        }

        return eventMapper.toResponse(eventRepository.save(event));
    }
}
