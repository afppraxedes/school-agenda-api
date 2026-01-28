package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.MessageRequest;
import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.service.MessageService;
import com.schoolagenda.domain.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final S3Service s3Service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'RESPONSIBLE', 'STUDENT')")
    public ResponseEntity<MessageResponse> send(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(request, currentUser.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inbox")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getInbox(
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        return ResponseEntity.ok(messageService.getInbox(currentUser.getId()));
    }

    @GetMapping("/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getSent(
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        return ResponseEntity.ok(messageService.getSentMessages(currentUser.getId()));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        return ResponseEntity.ok(messageService.countUnreadMessages(currentUser.getId()));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        messageService.markAsRead(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'RESPONSIBLE', 'STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id) {
        // Sua lógica de serviço para buscar a mensagem por ID
        MessageResponse response = messageService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/send-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendWithAttachment(
            @RequestPart("message") MessageRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        String fileUrl = null;
        String fileName = null;

        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            // Geramos um nome único para o S3 (ex: UUID + nome)
            String s3Key = UUID.randomUUID().toString() + "_" + fileName;
            fileUrl = s3Service.uploadFile(s3Key, file);
        }

        MessageResponse response = messageService.saveWithAttachment(request, fileUrl, fileName);
        return ResponseEntity.ok(response);
    }
}