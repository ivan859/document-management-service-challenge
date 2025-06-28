package com.clara.ops.challenge.document_management_service_challenge.documents.services;

import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Tag;
import com.clara.ops.challenge.document_management_service_challenge.documents.repositories.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.documents.repositories.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.documents.validation.UploadRequestValidator;
import com.clara.ops.challenge.document_management_service_challenge.documents.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.DocumentSearchFilters;
import org.openapitools.model.PaginatedDocumentSearch;
import org.openapitools.model.UploadDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private MinioService minioService;

    @Mock
    private UploadRequestValidator uploadRequestValidator;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MultipartFile multipartFile;

    private DocumentService documentService;
    private UploadDocument uploadDocument;
    private Document testDocument;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
            minioService, 
            uploadRequestValidator, 
            documentRepository, 
            tagRepository
        );

        uploadDocument = new UploadDocument();
        uploadDocument.setUser("testuser");
        uploadDocument.setName("test-document.pdf");
        uploadDocument.setTags(Arrays.asList("tag1", "tag2"));

        testDocument = new Document();
        testDocument.setId(UUID.randomUUID());
        testDocument.setUserName("testuser");
        testDocument.setDocumentName("test-document.pdf");
        testDocument.setCreatedAt(OffsetDateTime.now());
        testDocument.setFileSize(1024L);
        testDocument.setFileType("application/pdf");
        testDocument.setMinioPath("testuser/test-document.pdf");
        testDocument.setTags(new HashSet<>());
    }

    @Test
    void uploadDocument_validRequest_shouldUploadSuccessfully() {
        // Arrange
        when(uploadRequestValidator.validateUploadRequest(uploadDocument, multipartFile))
            .thenReturn(ValidationResult.valid());
        when(multipartFile.getSize()).thenReturn(1024L);
        
        Tag tag1 = new Tag();
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setName("tag2");
        
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag2);
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // Act
        documentService.uploadDocument(uploadDocument, multipartFile);

        // Assert
        verify(uploadRequestValidator).validateUploadRequest(uploadDocument, multipartFile);
        verify(minioService).uploadFile(eq("testuser/test-document.pdf"), eq(multipartFile));
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadDocument_invalidRequest_shouldThrowException() {
        // Arrange
        ValidationResult invalidResult = ValidationResult.invalid("File too large");
        when(uploadRequestValidator.validateUploadRequest(uploadDocument, multipartFile))
            .thenReturn(invalidResult);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> documentService.uploadDocument(uploadDocument, multipartFile)
        );

        assertEquals("File too large", exception.getMessage());
        verify(minioService, never()).uploadFile(any(), any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    void searchDocuments_withFilters_shouldUseSpecification() {
        // Arrange
        DocumentSearchFilters filters = new DocumentSearchFilters();
        filters.setUser(JsonNullable.of("testuser"));
        
        Page<Document> mockPage = new PageImpl<>(Arrays.asList(testDocument));
        when(documentRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(mockPage);

        // Act
        PaginatedDocumentSearch result = documentService.searchDocuments(
            filters, 0, 10, "asc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDocuments().size());
        verify(documentRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchDocuments_withoutFilters_shouldFindAll() {
        // Arrange
        Page<Document> mockPage = new PageImpl<>(Arrays.asList(testDocument));
        when(documentRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        // Act
        PaginatedDocumentSearch result = documentService.searchDocuments(
            null, 0, 10, "asc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDocuments().size());
        verify(documentRepository).findAll(any(Pageable.class));
        verify(documentRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void downloadDocument_existingDocument_shouldReturnPresignedUrl() {
        // Arrange
        UUID documentId = UUID.randomUUID();
        testDocument.setMinioPath("testuser/test-document.pdf");
        String presignedUrl = "https://minio.example.com/presigned-url";
        
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(testDocument));
        when(minioService.generatePresignedDownloadUrl("testuser/test-document.pdf")).thenReturn(presignedUrl);

        // Act
        String result = documentService.downloadDocument(documentId.toString());

        // Assert
        assertEquals(presignedUrl, result);
        verify(documentRepository).findById(documentId);
        verify(minioService).generatePresignedDownloadUrl("testuser/test-document.pdf");
    }

    @Test
    void downloadDocument_nonExistentDocument_shouldThrowException() {
        // Arrange
        UUID documentId = UUID.randomUUID();
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> documentService.downloadDocument(documentId.toString())
        );

        assertTrue(exception.getMessage().contains("Document not found"));
        verify(minioService, never()).generatePresignedDownloadUrl(any());
    }
}