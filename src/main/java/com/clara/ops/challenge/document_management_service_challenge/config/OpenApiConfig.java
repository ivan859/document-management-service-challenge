package com.clara.ops.challenge.document_management_service_challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Document Management Service API")
						.description("A comprehensive API for managing documents with upload, search, and download capabilities. " +
								"This service integrates with MinIO for file storage and PostgreSQL for metadata management.")
						.version("v0.0.1")
						.contact(new Contact()
								.name("Document Management Team")
								.email("support@documentmanagement.com"))
						.license(new License()
								.name("MIT License")
								.url("https://opensource.org/licenses/MIT")))
				.servers(List.of(
						new Server()
								.url("http://localhost:8081")
								.description("Local Development Server"),
						new Server()
								.url("/")
								.description("Default Server URL")
				));
	}
} 