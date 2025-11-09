package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.model.Conversation.ReadStatus;
import java.time.LocalDateTime;

public class ConversationResponse {

    private Long id;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    private Long studentId;
    private String studentName;
    private String studentClass;
    private String subject;
    private String content;
    private String attachmentPath;
    private LocalDateTime sentAt;
    private ReadStatus readStatus;

    // Constructors
    public ConversationResponse() {}

    public ConversationResponse(Long id, Long senderId, String senderName, String senderEmail,
                                Long recipientId, String recipientName, String recipientEmail,
                                Long studentId, String studentName, String studentClass,
                                String subject, String content, String attachmentPath,
                                LocalDateTime sentAt, ReadStatus readStatus) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.subject = subject;
        this.content = content;
        this.attachmentPath = attachmentPath;
        this.sentAt = sentAt;
        this.readStatus = readStatus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public ReadStatus getReadStatus() { return readStatus; }
    public void setReadStatus(ReadStatus readStatus) { this.readStatus = readStatus; }
}
