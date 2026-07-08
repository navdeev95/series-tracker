package io.github.nikoir.common.dto.response;

import java.util.Map;

public record SeriesListViewRs(String title,
                               Integer year,
                               String posterUrl,
                               Integer totalSeasons,
                               Map<ExternalId, String> externalIds,
                               boolean isSeries) {
}
