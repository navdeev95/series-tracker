package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByIsoCode(String isoCode);
    List<Country> findByIsoCodeIn(List<String> isoCodes);
}
