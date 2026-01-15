package io.lunov.backend.specification;

import io.lunov.backend.model.dto.session.SessionSearchDTO;
import io.lunov.backend.model.entity.Session;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SessionSpecification {

    public static Specification<Session> withFilter(SessionSearchDTO filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getAccessType() != null && !filters.getAccessType().name().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("accessType"), filters.getAccessType()));
            }

            if (filters.getClientName() != null && !filters.getClientName().trim().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("clientName")),
                        "%" + filters.getClientName().trim().toLowerCase() + "%"
                ));
            }

            if (filters.getContentType() != null && !filters.getContentType().name().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("contentType"), filters.getContentType()));
            }

            if (filters.getCreatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filters.getCreatedAfter()
                ));
            }

            if (filters.getCreatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filters.getCreatedBefore()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

    }
}
