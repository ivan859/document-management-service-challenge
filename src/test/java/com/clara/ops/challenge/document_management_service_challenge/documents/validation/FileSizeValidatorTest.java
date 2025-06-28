package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.config.FileUploadProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileSizeValidatorTest {

    @Mock
    private FileUploadProperties fileUploadProperties;

    @Mock
    private MultipartFile multipartFile;

    private FileSizeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FileSizeValidator(fileUploadProperties);
        when(fileUploadProperties.getMaxSizeBytes()).thenReturn(1024L * 1024L); // 1MB
        when(fileUploadProperties.getMaxSizeFormatted()).thenReturn("1.0MB");
    }


    @Test
    void validate_fileSizeExceedsLimit_shouldReturnInvalid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(2L * 1024L * 1024L); // 2MB
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("File size (2.0MB) exceeds maximum allowed size (1.0MB)"));
    }


    @Test
    void validate_largeFileSize_shouldFormatCorrectly() {
        when(fileUploadProperties.getMaxSizeBytes()).thenReturn(100L * 1024L * 1024L); // 100MB
        when(fileUploadProperties.getMaxSizeFormatted()).thenReturn("100.0MB");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(2L * 1024L * 1024L * 1024L); // 2GB
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("2.0GB"));
        assertTrue(result.getFirstError().contains("100.0MB"));
    }
}