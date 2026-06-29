package io.github.nikoir.series.tracker.common.dto.response;

public record SeriesDetailPersonalizedRs(
        SeriesDetailViewRs seriesInfo,
        SubscriptionStatus subscriptionStatus
) {
    //TODO: переименовать
    public enum SubscriptionStatus {
        SUBSCRIBED,
        AVAILABLE,
        NOT_AVAILABLE
    }
}
