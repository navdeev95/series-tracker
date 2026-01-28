package io.github.nikoir.series.tracker.adapter.series;

import io.github.nikoir.series.tracker.dto.integration.response.kinopoisk.KinopoiskExternalId;
import io.github.nikoir.series.tracker.enums.ExternalId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.nikoir.series.tracker.enums.ExternalId.KINOPOISK;

@Component
public class KinopoiskExternalIdAdapter {
    public Map<ExternalId, String> mapExternalIds(String kinopoiskId,
                                                  Map<String, String> externalIds) {
        Map<ExternalId, String> result = new HashMap<>();

        result.put(KINOPOISK, kinopoiskId);

        Optional.ofNullable(externalIds)
                .ifPresent(externalIdMap -> {
                    for (String name: externalIdMap.keySet()) {
                        String value = externalIdMap.get(name);

                        KinopoiskExternalId
                                .getExternalIdFromName(name)
                                .ifPresent(externalId -> result.put(externalId, value));

                    }
                });

        return result;
    }
}
