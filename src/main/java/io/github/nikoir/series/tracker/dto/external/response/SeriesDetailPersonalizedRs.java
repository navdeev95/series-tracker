package io.github.nikoir.series.tracker.dto.external.response;

public record SeriesDetailPersonalizedRs(
        SeriesDetailViewRs seriesInfo,
        boolean isUserSubscribed
) {
}
