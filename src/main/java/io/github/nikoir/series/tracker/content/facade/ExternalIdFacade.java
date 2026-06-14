package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.strategy.impl.TMDBExternalIdStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.WikidataExternalIdStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalIdFacade {
    private final TMDBExternalIdStrategy tmdbExternalIdStrategy;
    private final WikidataExternalIdStrategy wikidataExternalIdStrategy;

    public Map<ExternalId, String> enrichExternalIds(Map<ExternalId, String> existingExternalIds) {
        Map<ExternalId, String> tmdbResult = tmdbExternalIdStrategy.enrichExternalIds(existingExternalIds);
        return wikidataExternalIdStrategy.enrichExternalIds(tmdbResult);
    }
}
