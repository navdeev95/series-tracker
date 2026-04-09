package io.github.nikoir.series.tracker.common.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SeriesSearchRq(
        String title,

        @PositiveOrZero
        int page,

        @Positive
        int limit
) {
}
