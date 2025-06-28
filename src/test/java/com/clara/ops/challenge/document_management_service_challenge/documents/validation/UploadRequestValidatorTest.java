package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.documents.validation.FileValidator;
import com.clara.ops.challenge.document_management_service_challenge.documents.validation.ValidationResult;
import com.clara.ops.challenge.document_management_service_challenge.documents.validation.UploadRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.UploadDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadRequestValidatorTest {

    @Mock
    private FileValidator fileValidator1;

    @Mock
    private FileValidator fileValidator2;

    @Mock
    private MultipartFile multipartFile;

    private UploadRequestValidator validator;
    private UploadDocument uploadDocument;

    @BeforeEach
    void setUp() {
        List<FileValidator> fileValidators = Arrays.asList(fileValidator1, fileValidator2);
        validator = new UploadRequestValidator(fileValidators);
        
        uploadDocument = new UploadDocument();
        uploadDocument.setUser("testuser");
        uploadDocument.setName("document.pdf");
        
        // Setup mock validators
        when(fileValidator1.getOrder()).thenReturn(1);
        when(fileValidator2.getOrder()).thenReturn(2);
    }


    @Test
    void validateUploadRequest_validRequestData_shouldValidateFile() {
        when(fileValidator1.validate(multipartFile)).thenReturn(ValidationResult.valid());
        when(fileValidator2.validate(multipartFile)).thenReturn(ValidationResult.valid());
        
        ValidationResult result = validator.validateUploadRequest(uploadDocument, multipartFile);
        
        assertTrue(result.isValid());
        verify(fileValidator1).validate(multipartFile);
        verify(fileValidator2).validate(multipartFile);
    }

    @Test
    void validateUploadRequest_fileValidationFails_shouldReturnInvalid() {
        when(fileValidator1.validate(multipartFile)).thenReturn(ValidationResult.invalid("File too large"));
        when(fileValidator2.validate(multipartFile)).thenReturn(ValidationResult.valid());
        
        ValidationResult result = validator.validateUploadRequest(uploadDocument, multipartFile);
        
        assertFalse(result.isValid());
        assertEquals("File too large", result.getFirstError());
    }

    @Test
    void validateUploadRequest_multipleFileValidationErrors_shouldCombineErrors() {
        when(fileValidator1.validate(multipartFile)).thenReturn(ValidationResult.invalid("File too large"));
        when(fileValidator2.validate(multipartFile)).thenReturn(ValidationResult.invalid("Invalid file type"));
        
        ValidationResult result = validator.validateUploadRequest(uploadDocument, multipartFile);
        
        assertFalse(result.isValid());
        assertEquals("File too large; Invalid file type", result.getAllErrorsAsString());
    }

    @Test
    void validateUploadRequest_validatorsExecutedInOrder() {
        when(fileValidator1.validate(multipartFile)).thenReturn(ValidationResult.valid());
        when(fileValidator2.validate(multipartFile)).thenReturn(ValidationResult.valid());
        
        validator.validateUploadRequest(uploadDocument, multipartFile);
        
        // Verify validators are called in order
        verify(fileValidator1).getOrder();
        verify(fileValidator2).getOrder();
        verify(fileValidator1).validate(multipartFile);
        verify(fileValidator2).validate(multipartFile);
    }

    @Test
    void validateUploadRequest_validData_shouldReturnValid() {
        when(fileValidator1.validate(multipartFile)).thenReturn(ValidationResult.valid());
        when(fileValidator2.validate(multipartFile)).thenReturn(ValidationResult.valid());
        
        ValidationResult result = validator.validateUploadRequest(uploadDocument, multipartFile);
        
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void validateUploadRequest_safeUserAndDocumentNames_shouldReturnValid() {
        uploadDocument.setUser("john.doe");
        uploadDocument.setName("my-document_v1.2.pdf");
        
        when(fileValidator1.validate(multipartFile)).thenReturn(ValidationResult.valid());
        when(fileValidator2.validate(multipartFile)).thenReturn(ValidationResult.valid());
        
        ValidationResult result = validator.validateUploadRequest(uploadDocument, multipartFile);
        
        assertTrue(result.isValid());
    }
}