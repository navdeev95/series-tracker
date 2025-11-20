package io.github.nikoir.seriesparser.domain.repo;

import io.github.nikoir.seriesparser.domain.entity.Season;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    @EntityGraph(attributePaths = {"episodes"})
    @Query("SELECT s FROM Season s WHERE s.series.id = :seriesId")
    List<Season> findBySeriesIdWithEpisodes(@Param("seriesId") Long seriesId);
}
