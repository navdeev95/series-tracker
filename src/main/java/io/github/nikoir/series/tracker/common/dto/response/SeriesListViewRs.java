package io.github.nikoir.series.tracker.common.dto.response;

import io.github.nikoir.series.tracker.content.enums.ExternalId;

import java.util.Map;

public record SeriesListViewRs(String title,
                               Integer year,
                               String posterUrl,
                               Integer totalSeasons,
                               Map<ExternalId, String> externalIds,
                               boolean isSeries) {
}
