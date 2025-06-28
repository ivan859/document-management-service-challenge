package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.config.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MimeTypeValidator implements FileValidator {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public ValidationResult validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ValidationResult.valid(); // Let other validators handle empty files
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)) {
            return ValidationResult.invalid("File content type cannot be determined");
        }

        if (!fileUploadProperties.getAllowedMimeTypes().contains(contentType)) {
            return ValidationResult.invalid(
                String.format("File type '%s' is not allowed. Allowed types: %s", 
                    contentType, 
                    String.join(", ", fileUploadProperties.getAllowedMimeTypes()))
            );
        }

        return ValidationResult.valid();
    }

    @Override
    public int getOrder() {
        return 2; // Check MIME type after size
    }
}