package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.MessageRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.application.web.mapper.MessageMapper;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Assessment;
import com.schoolagenda.domain.model.Message;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.MessageRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public void deleteMessage(Long id) {
        // Busca o email do usuário logado no SecurityContext
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada de ID: " + id));

        // Regra de segurança: só deleta se o usuário estiver envolvido na mensagem
        boolean isOwner = message.getSender().getEmail().equals(currentUserEmail);
        boolean isRecipient = message.getRecipient().getEmail().equals(currentUserEmail);

        if (!isOwner && !isRecipient) {
            throw new BusinessException("Você não tem permissão para excluir esta mensagem");
        }

        messageRepository.delete(message);
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

//        Message msg = messageRepository.findById(userId).orElseThrow();
//        msg.setReadAt(OffsetDateTime.now(ZoneOffset.UTC)); // Isso faz o unread-count diminuir!
//        return messageRepository.save(msg);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse findById(Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        // Ajuste na segurança: permite acesso se o usuário for o remetente OU o destinatário
        Message message = messageRepository.findById(id)
                .filter(m -> m.getRecipient().getEmail().equals(email) ||
                        m.getSender().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada ou acesso negado"));

        return messageMapper.toResponse(message);
    }

    @Transactional
    public MessageResponse saveWithAttachment(MessageRequest request, String fileUrl, String originalFilename) {
        // 1. Obtém o remetente (usuário logado)
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Remetente não encontrado"));

        // 2. Obtém o destinatário
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatário não encontrado"));

        // 3. Cria a entidade e seta os valores
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setSubject(request.subject());
        message.setContent(request.content());
        message.setAttachmentUrl(fileUrl);
        message.setAttachmentName(originalFilename);
        message.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        message.setReadAt(null); // Mensagem nova começa como não lida

        // 4. Persiste no banco
        Message savedMessage = messageRepository.save(message);

        // 5. Retorna o DTO formatado
        return messageMapper.toResponse(savedMessage);
    }

//    private MessageResponse convertToResponse(Message msg) {
//        return new MessageResponse(
//                msg.getId(),
//                msg.getSender().getName(), // senderName
//                msg.getSubject(),
//                msg.getContent(),
//                msg.getCreatedAt().toString(), // sentAt
//                msg.isRead()
//        );
//    }
}
