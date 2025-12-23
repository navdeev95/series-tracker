package io.github.nikoir.series.tracker.domain.repo;

import io.github.nikoir.series.tracker.domain.entity.dictionary.DictExternalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalIdRepository extends JpaRepository<DictExternalId, Long> {
}
