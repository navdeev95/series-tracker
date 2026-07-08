package io.github.nikoir.tracker.content.service;

import io.github.nikoir.tracker.content.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.tracker.content.domain.repo.ExternalIdRepository;
import io.github.nikoir.common.dto.response.ExternalId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                fromEntity(entity)
                        .ifPresentOrElse(
                                value -> log.debug("Synced external id: {}", value.getName()),
                                () -> log.warn("Unknown external id in DB: {}", entity.getName()));
            });
        }
        catch (Exception e) {
            log.error("Failed to sync external id with database", e);
        }
    }

    private Optional<ExternalId> fromEntity(DictExternalId entity) {
        Optional<ExternalId> source = ExternalId.fromName(entity.getName());
        source.ifPresent(externalId ->  {
            externalId.setEntityId(entity.getId());
        });
        return source;
    }
}
