package io.github.nikoir.tracker.content.util;

import io.github.nikoir.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.common.dto.response.ExternalId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExternalIdUtils {
    public static Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIds) {
        Map<ExternalId, String> result = new HashMap<>();
        for (ExternalIdSeries externalId: externalIds) {
            ExternalId.fromId(externalId.getExternalId().getId())
                    .ifPresent(value -> result.put(value, externalId.getValue()));
        }
        return result;
    }
}
