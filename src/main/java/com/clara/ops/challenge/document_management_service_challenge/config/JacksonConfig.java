package com.clara.ops.challenge.document_management_service_challenge.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}