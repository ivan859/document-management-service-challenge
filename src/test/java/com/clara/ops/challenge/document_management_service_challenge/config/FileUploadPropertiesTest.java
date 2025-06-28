package com.clara.ops.challenge.document_management_service_challenge.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadPropertiesTest {

    @Test
    void defaultValues_shouldBeCorrect() {
        FileUploadProperties properties = new FileUploadProperties();
        
        assertEquals(500L * 1024L * 1024L, properties.getMaxSizeBytes());
        assertEquals(Set.of("application/pdf"), properties.getAllowedMimeTypes());
        assertEquals(Set.of(".pdf"), properties.getAllowedExtensions());
        assertEquals(255, properties.getMaxFilenameLength());
        assertFalse(properties.isVirusScanEnabled());
        assertTrue(properties.isContentValidationEnabled());
    }

    @Test
    void getMaxSizeFormatted_shouldReturnFormattedSize() {
        FileUploadProperties properties = new FileUploadProperties();
        
        assertEquals("500.0MB", properties.getMaxSizeFormatted());
    }

    @Test
    void setMaxSizeBytes_shouldUpdateFormattedSize() {
        FileUploadProperties properties = new FileUploadProperties();
        
        properties.setMaxSizeBytes(1024L * 1024L); // 1MB
        assertEquals("1.0MB", properties.getMaxSizeFormatted());
        
        properties.setMaxSizeBytes(2L * 1024L * 1024L * 1024L); // 2GB
        assertEquals("2.0GB", properties.getMaxSizeFormatted());
    }
}