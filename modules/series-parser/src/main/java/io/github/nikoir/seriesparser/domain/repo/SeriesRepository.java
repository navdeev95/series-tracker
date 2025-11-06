package io.github.nikoir.seriesparser.domain.repo;

import io.github.nikoir.seriesparser.domain.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {
    List<Series> findByTitle(String title);
}
