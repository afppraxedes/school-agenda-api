package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.MessageRequest;
import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Message;
import com.schoolagenda.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(MessageRequest request, Long senderId);
    void deleteMessage(Long id);
    List<MessageResponse> getInbox(Long userId);
    void markAsRead(Long messageId, Long userId);
    List<MessageResponse> getSentMessages(Long userId);
    long countUnreadMessages(Long userId);
    MessageResponse findById(Long id);
    MessageResponse saveWithAttachment(MessageRequest request, String fileUrl, String originalFilename);

}
