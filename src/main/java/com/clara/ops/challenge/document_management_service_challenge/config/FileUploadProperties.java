package com.clara.ops.challenge.document_management_service_challenge.config;

import com.clara.ops.challenge.document_management_service_challenge.utils.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.Set;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.file-upload")
public class FileUploadProperties {

    @Positive
    private long maxSizeBytes = 500L * 1024L * 1024L; // 500MB

    @NotEmpty
    private Set<String> allowedMimeTypes = Set.of("application/pdf");

    @NotEmpty
    private Set<String> allowedExtensions = Set.of(".pdf");

    @Positive
    private int maxFilenameLength = 255;

    private boolean virusScanEnabled = false;

    private boolean contentValidationEnabled = true;

    public String getMaxSizeFormatted() {
        return FileUtils.formatBytes(maxSizeBytes);
    }
}