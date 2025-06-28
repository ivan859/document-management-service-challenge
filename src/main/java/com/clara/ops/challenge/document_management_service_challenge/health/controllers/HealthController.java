package com.clara.ops.challenge.document_management_service_challenge.health.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {
    
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Simple health check endpoint to verify the service is running"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public String health() {
        return "OK";
    }
}
