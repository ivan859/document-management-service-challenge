package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.config.FileUploadProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MimeTypeValidatorTest {

    @Mock
    private FileUploadProperties fileUploadProperties;

    @Mock
    private MultipartFile multipartFile;

    private MimeTypeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MimeTypeValidator(fileUploadProperties);
        when(fileUploadProperties.getAllowedMimeTypes()).thenReturn(Set.of("application/pdf", "image/png"));
    }

    @Test
    void validate_allowedMimeType_shouldReturnValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }

    @Test
    void validate_anotherAllowedMimeType_shouldReturnValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/png");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }

    @Test
    void validate_disallowedMimeType_shouldReturnInvalid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("text/plain");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("File type 'text/plain' is not allowed"));
        assertTrue(result.getFirstError().contains("application/pdf"));
        assertTrue(result.getFirstError().contains("image/png"));
    }

    @Test
    void validate_caseSensitiveMimeType_shouldReturnInvalid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("APPLICATION/PDF"); // Uppercase
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("APPLICATION/PDF"));
    }

    @Test
    void validate_errorMessage_shouldListAllAllowedTypes() {
        when(fileUploadProperties.getAllowedMimeTypes()).thenReturn(Set.of("application/pdf", "image/png", "image/jpeg"));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("text/html");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        String errorMessage = result.getFirstError();
        assertTrue(errorMessage.contains("application/pdf"));
        assertTrue(errorMessage.contains("image/png"));
        assertTrue(errorMessage.contains("image/jpeg"));
    }

    @Test
    void validate_singleAllowedType_shouldWorkCorrectly() {
        when(fileUploadProperties.getAllowedMimeTypes()).thenReturn(Set.of("application/pdf"));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/png");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("image/png"));
        assertTrue(result.getFirstError().contains("application/pdf"));
    }
}