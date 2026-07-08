package io.github.nikoir.tracker.content.domain.repo;

import io.github.nikoir.tracker.content.domain.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    // Находим сезоны, у которых есть эпизоды без релизов
    @Query("SELECT DISTINCT s " +
            "FROM Season s " +
            "JOIN s.episodes e " +
            "WHERE s.series.id = :seriesId " +
            "AND e.releases IS EMPTY")
    List<Season> findSeasonsWithEpisodesWithoutReleases(@Param("seriesId") Long seriesId);

    List<Season> findBySeriesId(Long seriesId);
}
