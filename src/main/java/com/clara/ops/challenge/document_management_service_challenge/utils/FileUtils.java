package com.clara.ops.challenge.document_management_service_challenge.utils;

public final class FileUtils {
    
    private FileUtils() {
        // Utility class
    }
    
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        
        String[] units = {"bytes", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return unitIndex == 0 ? 
            String.format("%d %s", (long) size, units[unitIndex]) :
            String.format("%.1f%s", size, units[unitIndex]);
    }
}