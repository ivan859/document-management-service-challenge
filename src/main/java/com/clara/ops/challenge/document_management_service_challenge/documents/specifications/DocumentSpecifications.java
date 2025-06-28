package com.clara.ops.challenge.document_management_service_challenge.documents.specifications;

import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.documents.entities.Tag;
import com.clara.ops.challenge.document_management_service_challenge.documents.services.DocumentSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecifications {

    public static Specification<Document> withCriteria(DocumentSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.hasUserName()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("userName")),
                        "%" + criteria.getUserName().toLowerCase() + "%"
                ));
            }

            if (criteria.hasDocumentName()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("documentName")),
                        "%" + criteria.getDocumentName().toLowerCase() + "%"
                ));
            }

            if (criteria.hasTags()) {
                Join<Document, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(tagJoin.get("name").in(criteria.getTags()));
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}