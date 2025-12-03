package io.github.nikoir.seriesparser.dto.api.request;

import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.RequestParam;

public record SeriesSearchRq(
        @RequestParam
        String title,

        @RequestParam(required = false, defaultValue = "1")
        @Positive
        int page,

        @RequestParam(required = false, defaultValue = "10")
        @Positive
        int limit
) {
}
