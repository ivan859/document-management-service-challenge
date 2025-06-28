package com.clara.ops.challenge.document_management_service_challenge.documents.controllers;

import com.clara.ops.challenge.document_management_service_challenge.documents.services.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openapitools.model.DocumentSearchFilters;
import org.openapitools.model.PaginatedDocumentSearch;
import org.openapitools.model.UploadDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentController.class)
class DocumentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DocumentService documentService;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMultipartFile mockFile;
	private PaginatedDocumentSearch mockSearchResults;

	@BeforeEach
	void setUp() {
		mockFile = new MockMultipartFile(
				"file",
				"test.pdf",
				"application/pdf",
				"test content".getBytes()
		);

		mockSearchResults = new PaginatedDocumentSearch();
	}

	@Test
	void uploadDocument_ShouldReturnCreated_WhenUploadSuccessful() throws Exception {
		doNothing().when(documentService).uploadDocument(any(UploadDocument.class), any());

		mockMvc.perform(multipart("/documents/upload")
						.file(mockFile)
						.param("user", "testUser")
						.param("name", "testDocument.pdf")
						.param("tags", "tag1", "tag2"))
				.andExpect(status().isCreated());

		ArgumentCaptor<UploadDocument> captor = ArgumentCaptor.forClass(UploadDocument.class);
		verify(documentService).uploadDocument(captor.capture(), eq(mockFile));

		UploadDocument captured = captor.getValue();
		assertEquals("testUser", captured.getUser());
		assertEquals("testDocument.pdf", captured.getName());
		assertEquals(Arrays.asList("tag1", "tag2"), captured.getTags());
	}

	@Test
	void uploadDocument_ShouldReturnCreated_WhenUploadSuccessfulWithoutTags() throws Exception {
		doNothing().when(documentService).uploadDocument(any(UploadDocument.class), any());

		mockMvc.perform(multipart("/documents/upload")
						.file(mockFile)
						.param("user", "testUser")
						.param("name", "testDocument.pdf"))
				.andExpect(status().isCreated());

		verify(documentService).uploadDocument(any(UploadDocument.class), eq(mockFile));
	}

	@Test
	void uploadDocument_ShouldReturnBadRequest_WhenUserMissing() throws Exception {
		mockMvc.perform(multipart("/documents/upload")
						.file(mockFile)
						.param("name", "testDocument.pdf"))
				.andExpect(status().isBadRequest());

		verify(documentService, never()).uploadDocument(any(UploadDocument.class), any());
	}

	@Test
	void uploadDocument_ShouldReturnBadRequest_WhenNameMissing() throws Exception {
		mockMvc.perform(multipart("/documents/upload")
						.file(mockFile)
						.param("user", "testUser"))
				.andExpect(status().isBadRequest());

		verify(documentService, never()).uploadDocument(any(UploadDocument.class), any());
	}

	@Test
	void uploadDocument_ShouldReturnBadRequest_WhenFileMissing() throws Exception {
		mockMvc.perform(multipart("/documents/upload")
						.param("user", "testUser")
						.param("name", "testDocument.pdf"))
				.andExpect(status().isBadRequest());

		verify(documentService, never()).uploadDocument(any(UploadDocument.class), any());
	}

	@Test
	void downloadDocument_ShouldReturnDownloadUrl_WhenDocumentExists() throws Exception {
		String documentId = "test-document-id";
		String downloadUrl = "https://presigned-url";
		when(documentService.downloadDocument(documentId)).thenReturn(downloadUrl);

		mockMvc.perform(get("/document-management/download/{documentId}", documentId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.url").value(downloadUrl));

		verify(documentService).downloadDocument(documentId);
	}

	@Test
	void downloadDocument_ShouldReturn500_WhenDocumentNotFound() throws Exception {
		String documentId = "non-existent-id";
		when(documentService.downloadDocument(documentId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found"));

		mockMvc.perform(get("/document-management/download/{documentId}", documentId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(documentService).downloadDocument(documentId);
	}
}