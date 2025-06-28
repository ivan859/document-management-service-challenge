package com.clara.ops.challenge.document_management_service_challenge.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUtilsTest {

    @ParameterizedTest
    @CsvSource({
        "0, 0 bytes",
        "1, 1 bytes",
        "512, 512 bytes",
        "1023, 1023 bytes",
        "1024, 1.0KB",
        "1536, 1.5KB",
        "2048, 2.0KB",
        "1048576, 1.0MB",
        "1572864, 1.5MB",
        "524288000, 500.0MB",
        "1073741824, 1.0GB",
        "1610612736, 1.5GB",
        "1099511627776, 1.0TB"
    })
    void formatBytes_shouldFormatCorrectly(long bytes, String expected) {
        assertEquals(expected, FileUtils.formatBytes(bytes));
    }

    @Test
    void formatBytes_shouldHandleZero() {
        assertEquals("0 bytes", FileUtils.formatBytes(0));
    }

    @Test
    void formatBytes_shouldHandleMaxLong() {
        // Max long value should not crash
        String result = FileUtils.formatBytes(Long.MAX_VALUE);
        // Should contain TB and be reasonable
        assert result.contains("TB") || result.contains("GB");
    }

    @Test
    void formatBytes_shouldHandleNegativeValues() {
        // Negative values should still work (edge case)
        String result = FileUtils.formatBytes(-1024);
        assertEquals("-1024 bytes", result);
    }

    @Test
    void formatBytes_shouldFormatExactSizes() {
        assertEquals("1.0KB", FileUtils.formatBytes(1024L));
        assertEquals("1.0MB", FileUtils.formatBytes(1024L * 1024L));
        assertEquals("1.0GB", FileUtils.formatBytes(1024L * 1024L * 1024L));
        assertEquals("1.0TB", FileUtils.formatBytes(1024L * 1024L * 1024L * 1024L));
    }

    @Test
    void formatBytes_shouldFormatFractionalSizes() {
        assertEquals("1.5KB", FileUtils.formatBytes(1536L)); // 1.5 * 1024
        assertEquals("2.5MB", FileUtils.formatBytes(2621440L)); // 2.5 * 1024 * 1024
        assertEquals("1.2GB", FileUtils.formatBytes(1288490189L)); // ~1.2GB
    }
}