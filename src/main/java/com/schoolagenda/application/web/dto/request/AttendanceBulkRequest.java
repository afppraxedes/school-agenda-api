package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AttendanceBulkRequest(
        @NotNull Long timetableId, // O sistema já sabe a disciplina e a turma através do horário
        @NotNull LocalDate date,
        @NotNull List<StudentAttendanceRequest> students
) {}
