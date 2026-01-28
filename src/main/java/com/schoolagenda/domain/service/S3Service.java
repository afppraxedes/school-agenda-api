package com.schoolagenda.domain.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Template s3Template;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    public String uploadFile(String key, MultipartFile file) {
        try {
            // O S3Template cuida do InputStream e do Metadata automaticamente
            var resource = s3Template.upload(bucketName, key, file.getInputStream());

            // Retorna a URL pública (ou formatada) do arquivo
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar upload para S3", e);
        }
    }
}
