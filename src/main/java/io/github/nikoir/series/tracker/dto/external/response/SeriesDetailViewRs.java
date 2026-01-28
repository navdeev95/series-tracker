package io.github.nikoir.series.tracker.dto.external.response;

import io.github.nikoir.series.tracker.enums.ExternalId;

import java.util.List;
import java.util.Map;

public record SeriesDetailViewRs(
        String title,
        String engTitle,
        Integer totalSeasons,
        String status,
        Integer releaseYear,
        String posterUrl,
        String description,
        List<String> countries,
        Boolean isSeries,
        Map<ExternalId, String> externalIds,
        List<SeasonViewRs> seasons) {

}
