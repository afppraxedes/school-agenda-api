package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.SchoolEventRequest;
import com.schoolagenda.application.web.dto.response.SchoolEventResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.model.SchoolEvent;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

public interface SchoolEventService {

    List<SchoolEventResponse> getCalendar(OffsetDateTime start, OffsetDateTime end, AgendaUserDetails user);
    SchoolEventResponse create(SchoolEventRequest request);
    SchoolEventResponse update(Long id, SchoolEventRequest request);

}
