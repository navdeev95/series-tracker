package io.github.nikoir.series.tracker.domain.repo;

import io.github.nikoir.series.tracker.domain.entity.EpisodeRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeReleaseRepository extends JpaRepository<EpisodeRelease, Long> {
}
