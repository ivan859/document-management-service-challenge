package com.clara.ops.challenge.document_management_service_challenge.documents.repositories;

import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID>, JpaSpecificationExecutor<Document> {
    // All search functionality handled by JpaSpecificationExecutor
}