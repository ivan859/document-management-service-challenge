package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.config.FileUploadProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilenameValidatorTest {

    @Mock
    private FileUploadProperties fileUploadProperties;

    @Mock
    private MultipartFile multipartFile;

    private FilenameValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FilenameValidator(fileUploadProperties);
        when(fileUploadProperties.getMaxFilenameLength()).thenReturn(255);
        when(fileUploadProperties.getAllowedExtensions()).thenReturn(Set.of(".pdf", ".png"));
    }

    @Test
    void validate_validFilename_shouldReturnValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("document.pdf");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }

    @Test
    void validate_disallowedExtension_shouldReturnInvalid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("document.txt");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("File extension '.txt' is not allowed"));
        assertTrue(result.getFirstError().contains(".pdf"));
        assertTrue(result.getFirstError().contains(".png"));
    }

    @Test
    void validate_allowedExtension_shouldReturnValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("document.pdf");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }

    @Test
    void validate_caseInsensitiveExtension_shouldReturnValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("document.PDF");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }

    @Test
    void validate_multipleExtensions_shouldUseLastOne() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("document.backup.pdf");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }

    @Test
    void validate_validFilenameCharacters_shouldReturnValid() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("my-document_v1.2.pdf");
        
        ValidationResult result = validator.validate(multipartFile);
        
        assertTrue(result.isValid());
    }
}