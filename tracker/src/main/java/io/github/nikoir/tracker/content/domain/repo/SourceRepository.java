package io.github.nikoir.tracker.content.domain.repo;

import io.github.nikoir.tracker.content.domain.entity.dictionary.DictSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<DictSource, Long> {
    Optional<DictSource> findByName(String name);
}
