package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientIdAndArchivedByRecipientFalseOrderByCreatedAtDesc(Long recipientId);

    List<Message> findBySenderIdAndArchivedBySenderFalseOrderByCreatedAtDesc(Long senderId);

    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    // Validação de vínculo: O professor leciona para este aluno?
    @Query("""
        SELECT COUNT(tc) > 0 FROM TeacherClass tc 
        JOIN Student s ON s.schoolClass.id = tc.schoolClass.id
        WHERE tc.teacher.id = :teacherUserId AND s.id = :studentId
    """)
    boolean isTeacherOfStudent(
            @Param("teacherUserId") Long teacherUserId,
            @Param("studentId") Long studentId);
}
