package com.clara.ops.challenge.document_management_service_challenge.documents.services;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    
    private static final String BUCKET_NAME = "document-bucket";
    private static final int PRESIGNED_URL_EXPIRY_HOURS = 24;

    public void createBucketIfNotExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .build()
            );
            
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(BUCKET_NAME)
                    .build());
                log.info("Created bucket: {}", BUCKET_NAME);
            }
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create bucket", e);
        }
    }

    public void uploadFile(String objectName, MultipartFile file) {
        log.info("Uploading file: {}", objectName);
        log.info("File size: {}", file.getSize());
        log.info("File content type: {}", file.getContentType());
        log.info("File name: {}", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            log.info("File uploaded successfully: {}", objectName);
        } catch (Exception e) {
            log.error("Error uploading file {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + objectName, e);
        }
    }

    public String generatePresignedDownloadUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .expiry(PRESIGNED_URL_EXPIRY_HOURS, TimeUnit.HOURS)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error generating presigned URL for {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build()
            );
            log.info("File deleted successfully: {}", objectName);
        } catch (Exception e) {
            log.error("Error deleting file {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete file: " + objectName, e);
        }
    }

    public boolean doesFileExist(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getBucketName() {
        return BUCKET_NAME;
    }
}