package io.github.nikoir.series.tracker.domain.repo.specification;

import io.github.nikoir.series.tracker.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.series.tracker.enums.ExternalId;
import jakarta.persistence.criteria.Join;
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

            Join<Series, ExternalIdSeries> externalIdsJoin =
                    root.join("externalIds", JoinType.INNER);
            Join<ExternalIdSeries, DictExternalId> dictExternalIdJoin =
                    externalIdsJoin.join("externalId", JoinType.INNER);

            // Создаем OR условия для каждого externalId
            List<Predicate> predicates = externalIds.keySet().stream()
                    .map(externalId -> {
                        String value = externalIds.get(externalId);
                        return criteriaBuilder.and(
                                criteriaBuilder.equal(dictExternalIdJoin.get("name"),
                                        externalId.getName()),
                                criteriaBuilder.equal(externalIdsJoin.get("value"), value)
                        );
                    })
                    .toList();

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
