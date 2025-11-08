package io.github.nikoir.seriesparser.dto.response;

public record SeriesViewRs(String title,
                           Integer year,
                           String posterUrl,
                           Integer totalSeasons,
                           boolean isSeries) {
}
