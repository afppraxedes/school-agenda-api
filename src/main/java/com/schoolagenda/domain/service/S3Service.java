package com.schoolagenda.domain.service;

import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

@Slf4j
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
//            String anitized = sanitizeS3Key(key);
            // O S3Template cuida do InputStream e do Metadata automaticamente
            var resource = s3Template.upload(bucketName, key, file.getInputStream());

            // Retorna a URL pública (ou formatada) do arquivo
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar upload para S3", e);
        }
    }

    public String generatePresignedUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return null;

        try {
            String bucketName = "school-agenda-attachments";
            // Extrai apenas o nome do arquivo da URL completa
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // DECODIFICAÇÃO: Transforma "%20" em espaços reais para o SDK localizar o objeto
            String decodedKey = java.net.URLDecoder.decode(fileName, java.nio.charset.StandardCharsets.UTF_8);

            try (S3Presigner presigner = S3Presigner.create()) {
                GetObjectRequest objectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(decodedKey)
                        .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(objectRequest)
                        .build();

                // A URL gerada aqui já virá assinada e pronta para o navegador
                return presigner.presignGetObject(presignRequest).url().toString();
            }
        } catch (Exception e) {
            log.error("Erro ao gerar URL assinada: ", e);
            return fileUrl;
        }
    }

    /**
     * NÃO ESTÁ SENDO USADO ATUALMENTE
     * Corrige o nome do arquivo para compatibilidade com chaves de objeto do S3.
     * Resolve o erro de assinatura (Access Denied) causado por caracteres especiais.
     */
    public String sanitizeS3Key(String fileName) {
        if (fileName == null || fileName.isBlank()) return "";

        try {
            // 1. Faz o encoding padrão UTF-8
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

            // 2. Substitui o '+' gerado pelo Java pelo '%20' exigido pelo S3 (RFC 3986)
            return encoded.replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            // Fallback para nomes simples em caso de erro excepcional
            return fileName.replace(" ", "%20");
        }
    }
}
