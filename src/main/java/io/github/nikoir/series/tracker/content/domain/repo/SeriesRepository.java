package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long>, JpaSpecificationExecutor<Series> {

    @Query("SELECT s FROM Series s WHERE " +
            "s.title ILIKE %:searchTerm% OR " +
            "s.engTitle ILIKE %:searchTerm%")
    Page<Series> searchByTitleOrEngTitle(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s FROM Series s " +
            "JOIN FETCH s.externalIds ei " +
            "JOIN FETCH ei.externalId e WHERE " +
            "(:includeUnknownStatus = true AND s.status IS NULL) " +
            "OR (s.status IS NOT NULL AND (:#{#excludedStatus.isEmpty()} = true OR s.status NOT IN :excludedStatus)) " +
            "ORDER BY s.id ASC")
    Page<Series> searchSeriesWithStatus(@Param("includeUnknownStatus") boolean includeUnknownStatus,
                                        @Param("excludedStatus") List<Series.Status> excludedStatus,
                                        Pageable pageable);
}
