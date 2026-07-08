package io.github.nikoir.tracker.content.domain.repo;

import io.github.nikoir.tracker.content.domain.entity.EpisodeRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeReleaseRepository extends JpaRepository<EpisodeRelease, Long> {
}
