package io.github.nikoir.series.tracker.common.dto.response;

public record SeriesDetailPersonalizedRs(
        SeriesDetailViewRs seriesInfo,
        boolean isUserSubscribed
) {
}
