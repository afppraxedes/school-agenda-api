package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.ConversationRequest;
import com.schoolagenda.application.web.dto.response.ConversationResponse;
import com.schoolagenda.domain.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid @RequestBody ConversationRequest request) {
        ConversationResponse response = conversationService.createConversation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getAllConversations() {
        List<ConversationResponse> responses = conversationService.getAllConversations();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getConversationById(@PathVariable Long id) {
        ConversationResponse response = conversationService.getConversationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<ConversationResponse>> getConversationsBySender(
            @PathVariable Long senderId) {
        List<ConversationResponse> responses = conversationService.getConversationsBySender(senderId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<ConversationResponse>> getConversationsByRecipient(
            @PathVariable Long recipientId) {
        List<ConversationResponse> responses = conversationService.getConversationsByRecipient(recipientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ConversationResponse>> getConversationsByStudent(
            @PathVariable Long studentId) {
        List<ConversationResponse> responses = conversationService.getConversationsByStudent(studentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/between/{user1Id}/{user2Id}")
    public ResponseEntity<List<ConversationResponse>> getConversationsBetweenUsers(
            @PathVariable Long user1Id, @PathVariable Long user2Id) {
        List<ConversationResponse> responses = conversationService.getConversationsBetweenUsers(user1Id, user2Id);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recipient/{recipientId}/unread")
    public ResponseEntity<List<ConversationResponse>> getUnreadConversations(
            @PathVariable Long recipientId) {
        List<ConversationResponse> responses = conversationService.getUnreadConversations(recipientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recipient/{recipientId}/unread-count")
    public ResponseEntity<Long> getUnreadConversationCount(@PathVariable Long recipientId) {
        long count = conversationService.getUnreadConversationCount(recipientId);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/mark-unread")
    public ResponseEntity<ConversationResponse> markAsUnread(@PathVariable Long id) {
        ConversationResponse response = conversationService.markAsUnRead(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConversationResponse>> searchConversationsBySubject(
            @RequestParam String subject) {
        List<ConversationResponse> responses = conversationService.searchConversationsBySubject(subject);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<ConversationResponse> markAsRead(@PathVariable Long id) {
        ConversationResponse response = conversationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-multiple-read")
    public ResponseEntity<Void> markMultipleAsRead(@RequestBody List<Long> conversationIds) {
        conversationService.markMultipleAsRead(conversationIds);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConversationResponse> updateConversation(
            @PathVariable Long id, @Valid @RequestBody ConversationRequest request) {
        ConversationResponse response = conversationService.updateConversation(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }
}
