package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.ConversationRequest;
import com.schoolagenda.application.web.dto.response.ConversationResponse;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.model.Conversation;
import com.schoolagenda.domain.model.Conversation.ReadStatus;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.ConversationRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public ConversationResponse createConversation(ConversationRequest request) {
        // Find sender
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + request.getSenderId()));

        // Find recipient
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found with id: " + request.getRecipientId()));

        // Find student
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + request.getStudentId()));

        // Create and save conversation
        Conversation conversation = new Conversation(
                sender,
                recipient,
                student,
                request.getSubject(),
                request.getContent(),
                request.getAttachmentPath()
        );

        Conversation savedConversation = conversationRepository.save(conversation);
        return convertToResponse(savedConversation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversationById(Long id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + id));

        return convertToResponse(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getAllConversations() {
        List<Conversation> conversations = conversationRepository.findAll();

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversationsBySender(Long senderId) {
        List<Conversation> conversations = conversationRepository.findConversationsWithDetailsBySender(senderId);

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversationsByRecipient(Long recipientId) {
        List<Conversation> conversations = conversationRepository.findConversationsWithDetailsByRecipient(recipientId);

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversationsByStudent(Long studentId) {
        List<Conversation> conversations = conversationRepository.findByStudentIdOrderBySentAtDesc(studentId);

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversationsBetweenUsers(Long user1Id, Long user2Id) {
        List<Conversation> conversations = conversationRepository.findBySenderIdAndRecipientIdOrderBySentAtDesc(user1Id, user2Id);

        // Also get conversations where the users are reversed
        List<Conversation> reverseConversations = conversationRepository.findBySenderIdAndRecipientIdOrderBySentAtDesc(user2Id, user1Id);

        conversations.addAll(reverseConversations);

        // Sort by sent date descending
        conversations.sort((c1, c2) -> c2.getSentAt().compareTo(c1.getSentAt()));

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ConversationResponse> getUnreadConversations(Long recipientId) {
//        List<Conversation> conversations = conversationRepository.findByRecipientIdAndReadStatusOrderBySentAtDesc(recipientId, ReadStatus.UNREAD);
//
//        return conversations.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUnreadConversations(Long recipientId) {
        List<Conversation> conversations = conversationRepository.findUnreadConversationsByRecipient(recipientId);

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationResponse markAsRead(Long id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + id));

        conversation.setReadStatus(ReadStatus.READ);
        Conversation updatedConversation = conversationRepository.save(conversation);

        return convertToResponse(updatedConversation);
    }

    @Override
    @Transactional
    public void markMultipleAsRead(List<Long> conversationIds) {
        List<Conversation> conversations = conversationRepository.findAllById(conversationIds);

        for (Conversation conversation : conversations) {
            conversation.setReadStatus(ReadStatus.READ);
        }

        conversationRepository.saveAll(conversations);
    }

    @Override
    @Transactional
    public ConversationResponse updateConversation(Long id, ConversationRequest request) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + id));

        // Only allow updating content and attachment before it's read
        if (conversation.getReadStatus() == ReadStatus.UNREAD) {
            conversation.setContent(request.getContent());
            conversation.setAttachmentPath(request.getAttachmentPath());
        } else {
            throw new BusinessResourceException("Cannot update conversation that has been read");
        }

        Conversation updatedConversation = conversationRepository.save(conversation);
        return convertToResponse(updatedConversation);
    }

    @Override
    @Transactional
    public void deleteConversation(Long id) {
        if (!conversationRepository.existsById(id)) {
            throw new RuntimeException("Conversation not found with id: " + id);
        }
        conversationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadConversationCount(Long recipientId) {
        return conversationRepository.countByRecipientIdAndReadStatus(recipientId, ReadStatus.UNREAD);
    }

    @Override
    @Transactional
    public ConversationResponse markAsUnRead(Long id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + id));

        conversation.setReadStatus(ReadStatus.UNREAD);
        Conversation updatedConversation = conversationRepository.save(conversation);

        return convertToResponse(updatedConversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> searchConversationsBySubject(String subject) {
        List<Conversation> conversations = conversationRepository.findBySubjectContainingIgnoreCaseOrderBySentAtDesc(subject);

        return conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts Conversation entity to Response DTO
     */
    private ConversationResponse convertToResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getSender().getId(),
                conversation.getSender().getName(),
                conversation.getSender().getEmail(),
                conversation.getRecipient().getId(),
                conversation.getRecipient().getName(),
                conversation.getRecipient().getEmail(),
                conversation.getStudent().getId(),
//                conversation.getStudent().getName(),
                conversation.getStudent().getFullName(),
                conversation.getStudent().getClassName(), // Assuming Student has getClassName()
                conversation.getSubject(),
                conversation.getContent(),
                conversation.getAttachmentPath(),
                conversation.getSentAt(),
                conversation.getReadStatus()
        );
    }
}
