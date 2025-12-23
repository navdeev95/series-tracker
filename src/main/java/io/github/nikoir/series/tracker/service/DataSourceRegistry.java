package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.enums.Source;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceRegistry {
    private final SourceRepository sourceRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void syncWithDatabase() {
        try {
            List<DictSource> entities = sourceRepository.findAll();
            entities.forEach(entity -> {
                try {
                    Source source = Source.fromEntity(entity);

                    log.debug("Synced data source: {}", source.getName());
                } catch(IllegalArgumentException e) {
                    log.warn("Unknown data source in DB: {}", entity.getName());
                }
            });
        }
        catch (Exception e) {
            log.error("Failed to sync data sources with database", e);
        }
    }
}
