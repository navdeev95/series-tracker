package io.github.nikoir.seriesparser.dto.response;

import java.util.Map;

public record SeriesViewRs(String title,
                           Integer year,
                           String posterUrl,
                           Integer totalSeasons,
                           Map<String, String> externalIds,
                           boolean isSeries) {
}
