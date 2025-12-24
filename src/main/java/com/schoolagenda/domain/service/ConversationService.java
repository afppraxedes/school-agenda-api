package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.ConversationRequest;
import com.schoolagenda.application.web.dto.response.ConversationResponse;

import java.util.List;

public interface ConversationService {

    // Create a new conversation
    ConversationResponse createConversation(ConversationRequest request);

    // Get conversation by ID
    ConversationResponse getConversationById(Long id);

    // Get all conversations
    List<ConversationResponse> getAllConversations();

    // Get conversations by sender
    List<ConversationResponse> getConversationsBySender(Long senderId);

    // Get conversations by recipient
    List<ConversationResponse> getConversationsByRecipient(Long recipientId);

    // Get conversations by student
    List<ConversationResponse> getConversationsByStudent(Long studentId);

    // Get conversations between two users
    List<ConversationResponse> getConversationsBetweenUsers(Long user1Id, Long user2Id);

    // Get unread conversations for recipient
    List<ConversationResponse> getUnreadConversations(Long recipientId);

    // Mark conversation as read
    ConversationResponse markAsRead(Long id);

    // Mark multiple conversations as read
    void markMultipleAsRead(List<Long> conversationIds);

    // Update conversation
    ConversationResponse updateConversation(Long id, ConversationRequest request);

    // Delete conversation
    void deleteConversation(Long id);

    // Get unread conversation count for recipient
    long getUnreadConversationCount(Long recipientId);

    // Mark conversation as read
    ConversationResponse markAsUnRead(Long id);

    // Search conversations by subject
    List<ConversationResponse> searchConversationsBySubject(String subject);
}