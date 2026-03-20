package io.github.nikoir.series.tracker.common.dto.response;

import io.github.nikoir.series.tracker.content.dto.internal.SeriesStatus;
import io.github.nikoir.series.tracker.content.enums.ExternalId;

import java.util.List;
import java.util.Map;

public record SeriesDetailViewRs(
        Long id,
        String title,
        String engTitle,
        Integer totalSeasons,
        SeriesStatus status,
        Integer releaseYear,
        String posterUrl,
        String description,
        List<String> countries,
        Boolean isSeries,
        Map<ExternalId, String> externalIds) {

}
