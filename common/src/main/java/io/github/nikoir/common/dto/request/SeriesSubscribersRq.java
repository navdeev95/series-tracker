package io.github.nikoir.common.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SeriesSubscribersRq(
        @Positive
        Long seriesId,

        @PositiveOrZero
        int page,

        @Positive
        int limit
) {
}
