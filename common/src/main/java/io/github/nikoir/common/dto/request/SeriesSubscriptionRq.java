package io.github.nikoir.common.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SeriesSubscriptionRq(
        @Positive
        Long userTelegramId,

        @PositiveOrZero
        int page,

        @Positive
        int limit
) {
}
