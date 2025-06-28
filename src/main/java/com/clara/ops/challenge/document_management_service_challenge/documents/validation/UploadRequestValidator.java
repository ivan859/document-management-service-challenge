package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.UploadDocument;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UploadRequestValidator {

    private final List<FileValidator> fileValidators;

    public ValidationResult validateUploadRequest(UploadDocument uploadDocument, MultipartFile file) {
        ValidationResult result = validateRequestData(uploadDocument);
        
        if (!result.isValid()) {
            return result;
        }

        return validateFile(file);
    }

    private ValidationResult validateRequestData(UploadDocument uploadDocument) {
        if (uploadDocument == null) {
            return ValidationResult.invalid("Upload document request cannot be null");
        }

        if (!StringUtils.hasText(uploadDocument.getUser())) {
            return ValidationResult.invalid("User cannot be empty");
        }

        if (!StringUtils.hasText(uploadDocument.getName())) {
            return ValidationResult.invalid("Document name cannot be empty");
        }

        return ValidationResult.valid();
    }

    private ValidationResult validateFile(MultipartFile file) {
        // Sort validators by order and execute them
        return fileValidators.stream()
            .sorted(Comparator.comparingInt(FileValidator::getOrder))
            .map(validator -> validator.validate(file))
            .reduce(ValidationResult.valid(), ValidationResult::combine);
    }
}