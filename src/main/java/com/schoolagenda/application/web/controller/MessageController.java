package com.schoolagenda.application.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolagenda.application.web.dto.request.MessageRequest;
import com.schoolagenda.application.web.dto.response.MessageResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.service.MessageService;
import com.schoolagenda.domain.service.S3Service;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @PostMapping(value = "/upload-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAttachment(
            @RequestParam("file") MultipartFile file
    ) {
        // 1. Upload para o S3 (usando a lógica que já validamos)
        String fileName = file.getOriginalFilename();
        String s3Key = UUID.randomUUID() + "_" + fileName;
        String fileUrl = s3Service.uploadFile(s3Key, file);

        // 2. Retorna a URL e o nome original para o Frontend
        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("name", fileName);

        return ResponseEntity.ok(response);
    }

//    @PostMapping(value = "/send-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<MessageResponse> sendWithAttachment(
//            @RequestParam("message") String messageJson, // Recebe como String pura para evitar o 415
//            @RequestParam(value = "file", required = false) MultipartFile file
//    ) throws JsonProcessingException {
//
//        // 1. Parse manual do JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        // Garante que o Jackson entenda datas do Java 8
//        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
//
//        MessageRequest request = objectMapper.readValue(messageJson, MessageRequest.class);
//
//        // 2. Lógica de Upload para o S3
//        String fileName = (file != null) ? file.getOriginalFilename() : null;
//        String fileUrl = null;
//
//        if (file != null && !file.isEmpty()) {
//            // Gera um nome único para o S3
//            String s3Key = UUID.randomUUID() + "_" + fileName;
//            fileUrl = s3Service.uploadFile(s3Key, file); //
//        }
//
//        // 3. Persistência no Banco de Dados
//        MessageResponse response = messageService.saveWithAttachment(request, fileUrl, fileName);
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping(value = "/send-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<MessageResponse> sendWithAttachment(
//            @RequestParam("message") String messageJson, // Usamos RequestParam para aceitar como String pura
//            @RequestParam(value = "file", required = false) MultipartFile file
//    ) throws JsonProcessingException {
//
//        // Fazemos o parse manual. Isso elimina o erro 415 de vez.
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
//        MessageRequest request = objectMapper.readValue(messageJson, MessageRequest.class);
//
//        String fileName = (file != null) ? file.getOriginalFilename() : null;
//        String fileUrl = null;
//
//        if (file != null && !file.isEmpty()) {
//            fileUrl = s3Service.uploadFile(UUID.randomUUID() + "_" + fileName, file);
//        }
//
//        MessageResponse response = messageService.saveWithAttachment(request, fileUrl, fileName);
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping(value = "/send-with-attachment", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//    public ResponseEntity<MessageResponse> sendWithAttachment(
//            @RequestPart("message") Part messagePart, // Usamos Part do Servlet para extrair o conteúdo bruto
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ) throws Exception {
//
//        // Lendo a String de dentro da Part
//        String messageJson = new String(messagePart.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
//        MessageRequest request = objectMapper.readValue(messageJson, MessageRequest.class);
//
//        String fileName = (file != null) ? file.getOriginalFilename() : null;
//        String fileUrl = null;
//
//        if (file != null && !file.isEmpty()) {
//            fileUrl = s3Service.uploadFile(UUID.randomUUID() + "_" + fileName, file);
//        }
//
//        MessageResponse response = messageService.saveWithAttachment(request, fileUrl, fileName);
//        return ResponseEntity.ok(response);
//    }

//    @PreAuthorize("isAuthenticated()")
//    @PostMapping(value = "/send-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<MessageResponse> sendWithAttachment(
//            @RequestPart("message") String messageJson, // Recebemos como String pura
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ) {
//        try {
//            // Conversão manual do JSON usando o Jackson que você já tem no projeto
//            ObjectMapper objectMapper = new ObjectMapper();
//            // Registra suporte a Java 8 dates (importante para LocalDateTime)
//            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
//
//            MessageRequest request = objectMapper.readValue(messageJson, MessageRequest.class);
//
//            // Segue sua lógica original
//            String fileName = (file != null) ? file.getOriginalFilename() : null;
//            String fileUrl = null;
//
//            if (file != null && !file.isEmpty()) {
//                fileUrl = s3Service.uploadFile(UUID.randomUUID() + "_" + fileName, file);
//            }
//
//            MessageResponse response = messageService.saveWithAttachment(request, fileUrl, fileName);
//            return ResponseEntity.ok(response);
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Erro ao converter JSON da mensagem", e);
//        }
//    }

//    @PreAuthorize("isAuthenticated()")
//    @PostMapping(value = "/send-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<MessageResponse> sendWithAttachment(
//            @RequestPart("message") MessageRequest request,
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ) {
//        // Agora o Spring deve converter automaticamente sem erro 415
//        String fileName = (file != null) ? file.getOriginalFilename() : null;
//        String fileUrl = null;
//
//        if (file != null && !file.isEmpty()) {
//            fileName = file.getOriginalFilename();
//            // Geramos um nome único para o S3 (ex: UUID + nome)
//            String s3Key = UUID.randomUUID().toString() + "_" + fileName;
//            fileUrl = s3Service.uploadFile(s3Key, file);
//        }
//
//        MessageResponse response = messageService.saveWithAttachment(request, fileUrl, fileName);
//        return ResponseEntity.ok(response);
//    }
}