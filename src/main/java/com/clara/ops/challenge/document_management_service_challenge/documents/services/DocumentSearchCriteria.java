package com.clara.ops.challenge.document_management_service_challenge.documents.services;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.openapitools.model.DocumentSearchFilters;

import java.util.List;

@Getter
public class DocumentSearchCriteria {
    private final String userName;
    private final String documentName;
    private final List<String> tags;

    private DocumentSearchCriteria(String userName, String documentName, List<String> tags) {
        this.userName = userName;
        this.documentName = documentName;
        this.tags = tags;
    }

    @NotNull
    @Contract("null -> new")
    public static DocumentSearchCriteria from(DocumentSearchFilters filters) {
        if (filters == null) {
            return new DocumentSearchCriteria(null, null, null);
        }

        String userName = filters.getUser() != null && filters.getUser().isPresent() ? 
            filters.getUser().get() : null;
        String documentName = filters.getName() != null && filters.getName().isPresent() ? 
            filters.getName().get() : null;
        List<String> tags = filters.getTags() != null && filters.getTags().isPresent() ? 
            filters.getTags().get() : null;

        return new DocumentSearchCriteria(userName, documentName, tags);
    }

    public boolean hasUserName() {
        return StringUtils.hasText(userName);
    }

    public boolean hasDocumentName() {
        return StringUtils.hasText(documentName);
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public boolean hasAnyFilters() {
        return hasUserName() || hasDocumentName() || hasTags();
    }

}