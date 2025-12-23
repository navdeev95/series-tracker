package io.github.nikoir.series.tracker.dto.internal;

import io.github.nikoir.series.tracker.enums.ExternalId;

import java.util.List;
import java.util.Map;

public record SeriesDetailViewRs (
        String title,
        String engTitle,
        Integer totalSeasons,
        String status,
        Integer releaseYear,
        String posterUrl,
        Map<ExternalId, String> externalIds,
        List<SeasonViewRs> seasons) {

}
