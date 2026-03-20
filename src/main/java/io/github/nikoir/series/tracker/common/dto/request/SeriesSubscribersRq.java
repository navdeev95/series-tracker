package io.github.nikoir.series.tracker.common.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.RequestParam;

public record SeriesSubscribersRq(
        @Positive
        Long seriesId,

        @RequestParam(required = false, defaultValue = "0")
        @PositiveOrZero
        int page,

        @RequestParam(required = false, defaultValue = "10")
        @Positive
        int limit
) {
}
