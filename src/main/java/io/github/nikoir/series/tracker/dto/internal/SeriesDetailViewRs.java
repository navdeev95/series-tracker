package io.github.nikoir.series.tracker.dto.internal;

import java.util.List;
import java.util.Map;

public record SeriesDetailViewRs (
        String title,
        String engTitle,
        Integer totalSeasons,
        String status,
        Integer releaseYear,
        String posterUrl,
        Map<String, String> externalIds,
        List<SeasonViewRs> seasons) {

}
