package io.github.nikoir.series.tracker.content.strategy;

import io.github.nikoir.series.tracker.content.enums.ExternalId;

import java.util.Map;

public interface ExternalIdStrategy extends GettingStrategy {
    Map<ExternalId, String> enrichExternalIds(Map<ExternalId, String> externalIds);

    default void putIfNotBlankAndNotContains(Map<ExternalId, String> map, ExternalId key, String value) {
        if (value != null &&
                !value.isBlank() &&
                !map.containsKey(key)) {
            map.put(key, value);
        }
    }
}
