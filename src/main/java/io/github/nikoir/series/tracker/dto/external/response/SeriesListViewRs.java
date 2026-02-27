package io.github.nikoir.series.tracker.dto.external.response;

import io.github.nikoir.series.tracker.enums.ExternalId;

import java.util.Map;

public record SeriesListViewRs(String title,
                               Integer year,
                               String posterUrl,
                               Integer totalSeasons,
                               Map<ExternalId, String> externalIds,
                               boolean isSeries) {
}
