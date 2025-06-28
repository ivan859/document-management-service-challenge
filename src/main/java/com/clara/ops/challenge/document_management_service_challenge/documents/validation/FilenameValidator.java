package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import com.clara.ops.challenge.document_management_service_challenge.config.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class FilenameValidator implements FileValidator {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public ValidationResult validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ValidationResult.valid(); // Let other validators handle empty files
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename)) {
            return ValidationResult.invalid("Filename cannot be empty");
        }

        // Check filename length
        if (filename.length() > fileUploadProperties.getMaxFilenameLength()) {
            return ValidationResult.invalid(
                String.format("Filename length (%d) exceeds maximum allowed length (%d)", 
                    filename.length(), 
                    fileUploadProperties.getMaxFilenameLength())
            );
        }

        // Check file extension
        String extension = getFileExtension(filename);
        if (extension != null && !fileUploadProperties.getAllowedExtensions().contains(extension.toLowerCase())) {
            return ValidationResult.invalid(
                String.format("File extension '%s' is not allowed. Allowed extensions: %s", 
                    extension, 
                    String.join(", ", fileUploadProperties.getAllowedExtensions()))
            );
        }

        return ValidationResult.valid();
    }

    @Override
    public int getOrder() {
        return 0; // Check filename first as it's the most basic validation
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDotIndex);
    }
}