package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.response.AttachmentUrlResponse;

public interface AttachmentService {

    String generatePresignedUrl(String fileKey);
    AttachmentUrlResponse getPresignedUrl(String fileKey);

}
