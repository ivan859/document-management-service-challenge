package com.clara.ops.challenge.document_management_service_challenge.documents.repositories;

import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    
    Optional<Tag> findByName(String name);
}