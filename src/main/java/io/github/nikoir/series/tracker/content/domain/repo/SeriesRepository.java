package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long>, JpaSpecificationExecutor<Series> {
    @Query("SELECT DISTINCT s FROM Series s " +
            "LEFT JOIN FETCH s.countries " +
            "LEFT JOIN FETCH s.externalIds " +
            "WHERE s.id = :id")
    Optional<Series> findByIdWithCountries(@Param("id") Long id);

    @Query("SELECT s FROM Series s WHERE " +
            "s.title ILIKE %:searchTerm% OR " +
            "s.engTitle ILIKE %:searchTerm%")
    Page<Series> searchByTitleOrEngTitle(@Param("searchTerm") String searchTerm, Pageable pageable);

    @EntityGraph(attributePaths = {"externalIds.externalId"})
    @Query("SELECT DISTINCT s " +
            "FROM Series s " +
            "INNER JOIN s.seasons seasons" +
            "INNER JOIN seasons.episodes episodes " +
            "WHERE episodes.releases IS EMPTY " +
            "AND episodes.releaseDate >= CURRENT_DATE")
    Page<Series> searchSeriesWithoutReleases(Pageable pageable);

    @EntityGraph(attributePaths = {"externalIds.externalId"})
    @Query("SELECT DISTINCT s " +
            "FROM Series s " +
            "WHERE s.status IN :statuses AND " +
            "NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM Season season " +
            "    JOIN season.episodes episode " +
            "    WHERE season.series = s " +
            "    AND episode.releases IS EMPTY " +
            ")")
    Page<Series> searchSeriesWithCompletedSeasons(Pageable pageable, @Param("statuses") List<Series.Status> statuses);
}
