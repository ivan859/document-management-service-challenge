package com.clara.ops.challenge.document_management_service_challenge.documents.controllers;

import com.clara.ops.challenge.document_management_service_challenge.documents.services.DocumentService;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.DocumentManagementApi;
import org.openapitools.model.DocumentDownloadUrl;
import org.openapitools.model.DocumentSearchFilters;
import org.openapitools.model.PaginatedDocumentSearch;
import org.openapitools.model.UploadDocument;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DocumentController implements DocumentManagementApi {

    private final DocumentService documentService;

    @Override
    public ResponseEntity<Void> uploadDocument(@Valid UploadDocument uploadDocument) {
        throw new UnsupportedOperationException("Use the multipart endpoint instead");
    }

    @PostMapping(value = "/documents/upload", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadDocument(
            @RequestParam("user") String user,
            @RequestParam("name") String name,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam("file") MultipartFile file) {
        
        log.info("Received upload request for document: {} by user: {}", name, user);
        
        UploadDocument uploadDocument = new UploadDocument()
                .user(user)
                .name(name)
                .tags(tags);
        
        documentService.uploadDocument(uploadDocument, file);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<DocumentDownloadUrl> downloadDocument(String documentId) {
        log.info("Received download request for document ID: {}", documentId);
        
        String downloadUrl = documentService.downloadDocument(documentId);
        
        DocumentDownloadUrl response = new DocumentDownloadUrl().url(downloadUrl);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginatedDocumentSearch> searchDocuments(
            Integer page, 
            Integer size, 
            String sort,
            DocumentSearchFilters documentSearchFilters) {
        
        log.info("Received search request with filters: {}", documentSearchFilters);
        
        PaginatedDocumentSearch results = documentService.searchDocuments(
                documentSearchFilters, page, size, sort);
        
        return ResponseEntity.ok(results);
    }
}