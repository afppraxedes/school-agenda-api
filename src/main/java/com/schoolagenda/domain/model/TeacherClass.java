// src/main/java/com/schoolagenda/domain/model/TeacherClass.java
package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_classes")
public class TeacherClass extends BaseAuditableEntity {

    // TODO: FOI ALTERADA A TABELA, MAS NÃO FOI ALTERADA A ENTIDADE, REQUEST, RESPONSE E MAPPER! PEDIR AO "GEMINI" PARA CORRIGIR!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // NOVO: Linka o professor diretamente à disciplina que ele leciona
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // ALTERAÇÃO: De String para Relacionamento com SchoolClass
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    // Constructors
    public TeacherClass() {}

    public TeacherClass(User teacher, Subject subject, SchoolClass schoolClass) {
        this.teacher = teacher;
        this.subject = subject;
        this.schoolClass = schoolClass;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getTeacher() { return teacher; }
    public void setTeacher(User teacher) { this.teacher = teacher; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public SchoolClass getSchoolClass() { return schoolClass; }
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }
}