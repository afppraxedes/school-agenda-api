package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
public class Attendance extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id") // Novo vínculo com o horário
    private Timetable timetable;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean present; // true = Presença, false = Falta

    @Column(length = 255)
    private String note; // Para justificativas de falta (ex: atestado)
}