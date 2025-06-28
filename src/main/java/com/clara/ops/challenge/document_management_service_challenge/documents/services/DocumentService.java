package com.clara.ops.challenge.document_management_service_challenge.documents.services;

import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Tag;
import com.clara.ops.challenge.document_management_service_challenge.documents.repositories.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.documents.repositories.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.documents.services.MinioService;
import com.clara.ops.challenge.document_management_service_challenge.documents.specifications.DocumentSpecifications;
import com.clara.ops.challenge.document_management_service_challenge.documents.validation.UploadRequestValidator;
import com.clara.ops.challenge.document_management_service_challenge.documents.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final MinioService minioService;
    private final UploadRequestValidator uploadRequestValidator;
    private final DocumentRepository documentRepository;
    private final TagRepository tagRepository;

    public void uploadDocument(UploadDocument uploadDocument, MultipartFile file) {
        log.info("Starting upload process for document: {} by user: {}", 
                uploadDocument.getName(), uploadDocument.getUser());

        validateUploadRequest(uploadDocument, file);
        
        minioService.createBucketIfNotExists();
        
        String objectName = buildObjectName(uploadDocument.getUser(), uploadDocument.getName());
        
        minioService.uploadFile(objectName, file);
        
        Set<Tag> tags = createOrFindTags(uploadDocument.getTags());
        
        Document document = Document.builder()
                .userName(uploadDocument.getUser())
                .documentName(uploadDocument.getName())
                .minioPath(objectName)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .tags(tags)
                .build();
        
        documentRepository.save(document);
        
        log.info("Document uploaded successfully with ID: {}", document.getId());
    }

    public String downloadDocument(String documentId) {
        log.info("Generating download URL for document ID: {}", documentId);
        
        UUID uuid = UUID.fromString(documentId);
        Document document = documentRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found with ID: " + documentId));
        
        return minioService.generatePresignedDownloadUrl(document.getMinioPath());
    }

    public PaginatedDocumentSearch searchDocuments(
            DocumentSearchFilters filters, 
            Integer page, 
            Integer size, 
            String sort) {
        
        log.info("Searching documents with filters: {}", filters);
        
        Pageable pageable = buildPageable(page, size, sort);
        Page<Document> documentPage = findDocumentsWithFilters(filters, pageable);
        
        List<org.openapitools.model.Document> results = documentPage.getContent().stream()
                .map(this::mapToApiDocument)
                .collect(Collectors.toList());
        
        Metadata metadata = new Metadata()
                .currentPage(documentPage.getNumber())
                .itemsPerPage(documentPage.getSize())
                .currentItems(documentPage.getNumberOfElements())
                .totalPages(documentPage.getTotalPages())
                .totalItems((int) documentPage.getTotalElements());
        
        return new PaginatedDocumentSearch()
                .metadata(metadata)
                .documents(results);
    }

    private void validateUploadRequest(UploadDocument uploadDocument, MultipartFile file) {
        ValidationResult result = uploadRequestValidator.validateUploadRequest(uploadDocument, file);
        
        if (!result.isValid()) {
            log.warn("Upload validation failed: {}", result.getAllErrorsAsString());
            throw new IllegalArgumentException(result.getFirstError());
        }
        
        log.debug("Upload validation passed for user: {}, document: {}", 
            uploadDocument.getUser(), uploadDocument.getName());
    }

    private String buildObjectName(String user, String documentName) {
        return user + "/" + documentName;
    }

    private Set<Tag> createOrFindTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            if (StringUtils.hasText(tagName)) {
                Tag tag = tagRepository.findByName(tagName.trim())
                        .orElseGet(() -> {
                            Tag newTag = Tag.builder()
                                    .name(tagName.trim())
                                    .build();
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
        }
        return tags;
    }

    private Pageable buildPageable(Integer page, Integer size, String sort) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        Sort.Direction direction = sort != null && sort.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, "createdAt"));
    }

    private Page<Document> findDocumentsWithFilters(DocumentSearchFilters filters, Pageable pageable) {
        DocumentSearchCriteria criteria = DocumentSearchCriteria.from(filters);
        
        if (!criteria.hasAnyFilters()) {
            return documentRepository.findAll(pageable);
        }
        
        return documentRepository.findAll(DocumentSpecifications.withCriteria(criteria), pageable);
    }

    private org.openapitools.model.Document mapToApiDocument(Document document) {
        List<String> tagNames = document.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        
        return new org.openapitools.model.Document()
                .id(document.getId().toString())
                .user(document.getUserName())
                .name(document.getDocumentName())
                .tags(tagNames)
                .size(document.getFileSize().intValue())
                .type(document.getFileType())
                .createdAt(document.getCreatedAt().toString());
    }
}
