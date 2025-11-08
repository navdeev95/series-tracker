package io.github.nikoir.seriesparser.domain.repo;

import io.github.nikoir.seriesparser.domain.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {

    @Query("SELECT s FROM Series s WHERE " +
            "s.title ILIKE %:searchTerm% OR " +
            "s.engTitle ILIKE %:searchTerm%")
    Page<Series> searchByTitleOrEngTitle(@Param("searchTerm") String searchTerm, Pageable pageable);
}
