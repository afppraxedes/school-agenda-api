package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Conversation;
import com.schoolagenda.domain.model.Conversation.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Find conversations by sender
    List<Conversation> findBySenderIdOrderBySentAtDesc(Long senderId);

    // Find conversations by recipient
    List<Conversation> findByRecipientIdOrderBySentAtDesc(Long recipientId);

    // Find conversations by student
    List<Conversation> findByStudentIdOrderBySentAtDesc(Long studentId);

    // Find conversations between two users
    List<Conversation> findBySenderIdAndRecipientIdOrderBySentAtDesc(Long senderId, Long recipientId);

    // Find conversations by recipient and read status
    List<Conversation> findByRecipientIdAndReadStatusOrderBySentAtDesc(Long recipientId, ReadStatus readStatus);

    // Find unread conversations for recipient
    @Query("SELECT c FROM Conversation c WHERE c.recipient.id = :recipientId AND c.readStatus = 'UNREAD' ORDER BY c.sentAt DESC")
    List<Conversation> findUnreadConversationsByRecipient(@Param("recipientId") Long recipientId);

    // Find conversations by student and sender
    List<Conversation> findByStudentIdAndSenderIdOrderBySentAtDesc(Long studentId, Long senderId);

    // Find conversations by student and recipient
    List<Conversation> findByStudentIdAndRecipientIdOrderBySentAtDesc(Long studentId, Long recipientId);

    // Count unread conversations for recipient
    long countByRecipientIdAndReadStatus(Long recipientId, ReadStatus readStatus);

    // Find conversations sent after a certain date
    List<Conversation> findBySentAtAfterOrderBySentAtDesc(LocalDateTime date);

    // Find conversations by subject containing (search)
    List<Conversation> findBySubjectContainingIgnoreCaseOrderBySentAtDesc(String subject);

    // Find conversations with details (using JOIN FETCH)
    @Query("SELECT c FROM Conversation c JOIN FETCH c.sender JOIN FETCH c.recipient JOIN FETCH c.student WHERE c.recipient.id = :recipientId ORDER BY c.sentAt DESC")
    List<Conversation> findConversationsWithDetailsByRecipient(@Param("recipientId") Long recipientId);

    @Query("SELECT c FROM Conversation c JOIN FETCH c.sender JOIN FETCH c.recipient JOIN FETCH c.student WHERE c.sender.id = :senderId ORDER BY c.sentAt DESC")
    List<Conversation> findConversationsWithDetailsBySender(@Param("senderId") Long senderId);
}
