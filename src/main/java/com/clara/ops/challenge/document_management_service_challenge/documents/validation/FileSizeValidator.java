package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.config.FileUploadProperties;
import com.clara.ops.challenge.document_management_service_challenge.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileSizeValidator implements FileValidator {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public ValidationResult validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ValidationResult.invalid("File cannot be empty");
        }

        if (file.getSize() > fileUploadProperties.getMaxSizeBytes()) {
            return ValidationResult.invalid(
                String.format("File size (%s) exceeds maximum allowed size (%s)", 
                    FileUtils.formatBytes(file.getSize()), 
                    fileUploadProperties.getMaxSizeFormatted())
            );
        }

        return ValidationResult.valid();
    }

    @Override
    public int getOrder() {
        return 1; // Check size first as it's a quick validation
    }
}