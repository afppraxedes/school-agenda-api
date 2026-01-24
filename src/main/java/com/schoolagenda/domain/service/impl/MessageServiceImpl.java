package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.MessageRequest;
import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.application.web.mapper.MessageMapper;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Message;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.MessageRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Remetente não encontrado"));

        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatário não encontrado"));

        // Regra de Negócio: Se for professor, validar se ele tem aula com o aluno citado
        if (sender.getRoles().contains(UserRole.TEACHER) && request.studentId() != null) {
            if (!messageRepository.isTeacherOfStudent(senderId, request.studentId())) {
                throw new BusinessException("Você não possui vínculo acadêmico com este aluno para enviar mensagens.");
            }
        }

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setSubject(request.subject());
        message.setContent(request.content());

        if (request.studentId() != null) {
            message.setStudent(studentRepository.getReferenceById(request.studentId()));
        }

        return messageMapper.toResponse(messageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getInbox(Long userId) {
        return messageRepository.findByRecipientIdAndArchivedByRecipientFalseOrderByCreatedAtDesc(userId)
                .stream().map(messageMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada"));

        if (message.getRecipient().getId().equals(userId) && message.getReadAt() == null) {
            message.setReadAt(OffsetDateTime.now(ZoneOffset.UTC));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getSentMessages(Long userId) {
        return messageRepository.findBySenderIdAndArchivedBySenderFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }

//    @Override
//    @Transactional(readOnly = true)
//    public long countUnreadMessages(Long userId) {
//        return messageRepository.countByRecipientIdAndReadAtIsNull(userId);
//    }
    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessages(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }
}
