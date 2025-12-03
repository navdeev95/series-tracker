package io.github.nikoir.seriesparser.domain.repo;

import io.github.nikoir.seriesparser.domain.entity.EpisodeRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeReleaseRepository extends JpaRepository<EpisodeRelease, Long> {
}
