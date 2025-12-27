package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AttendanceBulkRequest(
        @NotNull Long subjectId,
        @NotNull Long schoolClassId,
        @NotNull LocalDate date,
        @NotEmpty List<StudentAttendanceRequest> attendances
) {}
