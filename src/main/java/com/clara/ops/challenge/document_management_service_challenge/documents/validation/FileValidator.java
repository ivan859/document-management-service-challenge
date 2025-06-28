package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {
    
    /**
     * Validates a file upload
     * @param file the file to validate
     * @return validation result containing any errors
     */
    ValidationResult validate(MultipartFile file);
    
    /**
     * Returns the order of this validator (lower values execute first)
     * @return the order value
     */
    int getOrder();
}