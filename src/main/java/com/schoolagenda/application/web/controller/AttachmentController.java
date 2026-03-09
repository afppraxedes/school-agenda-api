package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.response.AttachmentUrlResponse;
import com.schoolagenda.domain.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/presigned-url/{fileKey}")
    public ResponseEntity<AttachmentUrlResponse> getPresignedUrl(@PathVariable String fileKey) {
        // IMPORTANTE: Aqui você pode validar se o usuário tem permissão para acessar este arquivo
        String url = attachmentService.generatePresignedUrl(fileKey);
        return ResponseEntity.ok(new AttachmentUrlResponse(url));
    }
}
