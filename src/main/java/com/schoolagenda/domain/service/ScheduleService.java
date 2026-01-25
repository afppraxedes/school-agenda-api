package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.response.TimetableResponse;

import java.util.List;

public interface ScheduleService {

    List<TimetableResponse> findTodayByStudent(Long studentId);

}
