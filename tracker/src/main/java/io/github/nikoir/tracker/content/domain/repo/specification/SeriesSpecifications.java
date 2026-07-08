package io.github.nikoir.tracker.content.domain.repo.specification;

import io.github.nikoir.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.common.dto.response.ExternalId;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public class SeriesSpecifications {
    public static Specification<Series> hasAnyExternalIdFromList(Map<ExternalId, String> externalIds) {
        return (root, query, criteriaBuilder) -> {
            if (externalIds == null || externalIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Fetch<Series, ExternalIdSeries> externalIdFetch =
                    root.fetch("externalIds", JoinType.INNER);
            externalIdFetch.fetch("externalId", JoinType.INNER);

            // Создаем OR условия для каждого externalId
            List<Predicate> predicates = externalIds.keySet().stream()
                    .map(externalId -> {
                        String value = externalIds.get(externalId);
                        return criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("externalIds")
                                                .get("externalId")
                                                .get("name"), externalId.getName()),
                                criteriaBuilder.equal(root.get("externalIds")
                                        .get("value"), value));
                    }).toList();

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
