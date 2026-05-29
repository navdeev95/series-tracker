package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
}
