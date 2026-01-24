package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.TimetableRequest;
import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;

import java.util.List;

public interface TimetableService {

    TimetableResponse create(TimetableRequest request);
    TimetableResponse update(Long id, TimetableRequest request);
    List<TimetableResponse> findBySchoolClassId(Long schoolClassId);
    List<TimetableResponse> findByTeacherId(Long teacherId);
    void delete(Long id);
    TimetableResponse getNextClass(AgendaUserDetails currentUser);
    byte[] generateTimetablePdf(Long schoolClassId);
    TimetableResponse getCurrentOrNextClass(AgendaUserDetails currentUser);
    public List<TimetableResponse> getTodayScheduleForStudent(Long studentUserId);

}
