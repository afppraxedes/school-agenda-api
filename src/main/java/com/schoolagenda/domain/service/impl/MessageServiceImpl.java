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
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.MessageRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.MessageService;
import com.schoolagenda.domain.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final MessageMapper messageMapper;
    private final S3Service s3Service;

    @Override
//    @Transactional
//    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
//        User sender = userRepository.findById(senderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Remetente não encontrado"));
//
//        User recipient = userRepository.findById(request.recipientId())
//                .orElseThrow(() -> new ResourceNotFoundException("Destinatário não encontrado"));
//
//        Message message = new Message();
//        message.setSender(sender);
//        message.setRecipient(recipient);
//        message.setSubject(request.subject());
//        message.setContent(request.content());
//
//        // PERSISTÊNCIA DOS CAMPOS DE ANEXO
//        message.setAttachmentUrl(request.attachmentUrl());
//        message.setAttachmentName(request.attachmentName());
//
//        // NOVA LÓGICA PARA O STUDENT_ID
//        if (sender.getRoles().contains(UserRole.STUDENT)) {
//            // Se o remetente for aluno, buscamos o ID da tabela 'student' vinculado ao User dele
//            // Assumindo que você tem um método no repository para buscar o Student pelo User
//            Student student = studentRepository.findByUserId(senderId)
//                    .orElseThrow(() -> new BusinessException("Cadastro de aluno não encontrado para este usuário."));
//            message.setStudent(student);
//
//        } else if (sender.getRoles().contains(UserRole.TEACHER) && request.studentId() != null) {
//            // Se for professor, mantemos a validação de vínculo existente
//            if (!messageRepository.isTeacherOfStudent(senderId, request.studentId())) {
//                throw new BusinessException("Você não possui vínculo acadêmico com este aluno.");
//            }
//            message.setStudent(studentRepository.getReferenceById(request.studentId()));
//        }
//
//        return messageMapper.toResponse(messageRepository.save(message));
//    }

    @Transactional
    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
        // 1. Recuperação das entidades base (Remetente e Destinatário)
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Remetente não encontrado"));

        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatário não encontrado"));

        // 2. Instanciação e preenchimento básico da Mensagem
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setSubject(request.subject());
        message.setContent(request.content());

        // 3. Persistência dos metadados do anexo (URL fixa do S3 e Nome original)
        message.setAttachmentUrl(request.attachmentUrl());
        message.setAttachmentName(request.attachmentName());

        // 4. Lógica de Contexto do Aluno (student_id)
        if (sender.getRoles().contains(UserRole.STUDENT)) {
            // Se o remetente for ALUNO, ignoramos o que veio do front e buscamos o ID de negócio (ID 1) vinculado ao User (ID 23)
            Student student = studentRepository.findByUserId(senderId)
                    .orElseThrow(() -> new BusinessException("Cadastro de aluno não encontrado para este usuário."));
            message.setStudent(student);

        } else if (sender.getRoles().contains(UserRole.TEACHER) && request.studentId() != null) {
            // Se for PROFESSOR, validamos se ele tem aula com o aluno citado no payload
            if (!messageRepository.isTeacherOfStudent(senderId, request.studentId())) {
                throw new BusinessException("Você não possui vínculo acadêmico com este aluno.");
            }
            message.setStudent(studentRepository.getReferenceById(request.studentId()));
        }

        // 5. Persistência no banco de dados
        Message savedMessage = messageRepository.save(message);

        // 6. Conversão para DTO e Geração da URL Assinada para o retorno
        MessageResponse response = messageMapper.toResponse(savedMessage);

        if (savedMessage.getAttachmentUrl() != null) {
            // Transformamos a URL fixa em uma URL temporária e segura para visualização imediata
            response.setAttachmentUrl(s3Service.generatePresignedUrl(savedMessage.getAttachmentUrl()));
        }

        return response;
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

        // Mantém sua lógica de segurança original
        Message message = messageRepository.findById(id)
                .filter(m -> m.getRecipient().getEmail().equals(email) ||
                        m.getSender().getEmail().equals(email))
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada ou acesso negado"));

        MessageResponse response = messageMapper.toResponse(message);

        // Geramos a URL assinada apenas se houver o campo preenchido
        if (message.getAttachmentUrl() != null) {
            response.setAttachmentUrl(s3Service.generatePresignedUrl(message.getAttachmentUrl()));
        }

        return response;

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
