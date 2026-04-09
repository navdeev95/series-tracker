package io.github.nikoir.series.tracker.content.dto.internal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SeriesSyncRq (
    @PositiveOrZero
    int page,

    @Positive
    int limit
) {

}
