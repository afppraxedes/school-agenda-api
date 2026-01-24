package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.response.StudentDashboardResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;

public interface DashboardService {

    StudentDashboardResponse getStudentDashboard(AgendaUserDetails currentUser);

}
