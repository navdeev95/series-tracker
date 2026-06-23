package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    // Получаем эпизоды без релизов для конкретного сезона
    @Query("SELECT e FROM Episode e " +
            "WHERE e.season.id = :seasonId " +
            "AND e.releases IS EMPTY")
    List<Episode> findEpisodesWithoutReleasesBySeasonId(@Param("seasonId") Long seasonId);
}
