package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
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

    @EntityGraph(attributePaths = {"externalIds", "externalIds.externalId"})
    @Query("SELECT DISTINCT s FROM Series s WHERE " +
            "(:includeUnknownStatus = true AND s.status IS NULL) OR " +
            "(s.status IS NOT NULL AND (:#{#excludedStatus.isEmpty()} = true OR s.status NOT IN :excludedStatus))")
    Page<Series> searchSeriesWithStatus(@Param("includeUnknownStatus") boolean includeUnknownStatus,
                                        @Param("excludedStatus") List<Series.Status> excludedStatus,
                                        Pageable pageable);
}
