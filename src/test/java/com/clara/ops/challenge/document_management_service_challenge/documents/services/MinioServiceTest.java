package com.clara.ops.challenge.document_management_service_challenge.documents.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "test.pdf",


                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    void createBucketIfNotExists_ShouldCreateBucket_WhenBucketDoesNotExist() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        assertDoesNotThrow(() -> minioService.createBucketIfNotExists());

        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void createBucketIfNotExists_ShouldNotCreateBucket_WhenBucketExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        assertDoesNotThrow(() -> minioService.createBucketIfNotExists());

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void uploadFile_ShouldUploadSuccessfully() throws Exception {
        String objectName = "testUser/test.pdf";

        assertDoesNotThrow(() -> minioService.uploadFile(objectName, mockFile));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadFile_ShouldThrowException_WhenMinioFails() throws Exception {
        String objectName = "testUser/test.pdf";
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException("MinIO error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> minioService.uploadFile(objectName, mockFile)
        );

        assertTrue(exception.getMessage().contains("Failed to upload file"));
    }

    @Test
    void getBucketName_ShouldReturnCorrectBucketName() {
        assertEquals("document-bucket", minioService.getBucketName());
    }
}