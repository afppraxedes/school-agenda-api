package com.schoolagenda.domain.repository;

import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Busca uma mensagem específica garantindo que o destinatário seja o usuário logado
    Optional<Message> findByIdAndRecipientId(Long id, Long recipientId);

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

    @Query("""
        SELECT COUNT(m) 
        FROM Message m 
        WHERE m.recipient.id = :userId 
        AND m.readAt IS NULL
        AND m.deletedAt IS NULL
    """)
    long countUnreadMessages(@Param("userId") Long userId);
}
