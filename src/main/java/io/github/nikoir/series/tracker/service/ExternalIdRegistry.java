package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.series.tracker.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.enums.ExternalId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
//TODO: сделать общий компонент с DataSourceRegistry
public class ExternalIdRegistry {
    private final ExternalIdRepository externalIdRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void syncWithDatabase() {
        try {
            List<DictExternalId> entities = externalIdRepository.findAll();
            entities.forEach(entity -> {
                try {
                    ExternalId source = ExternalId.fromEntity(entity);

                    log.debug("Synced external id: {}", source.getName());
                } catch(IllegalArgumentException e) {
                    log.warn("Unknown external id in DB: {}", entity.getName());
                }
            });
        }
        catch (Exception e) {
            log.error("Failed to sync external id with database", e);
        }
    }
}
