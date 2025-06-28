package com.clara.ops.challenge.document_management_service_challenge.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioProperties.getUrl())
            .credentials(minioProperties.getUser(), minioProperties.getPassword())
            .build();
    }
}
