package io.github.nikoir.series.tracker.strategy.context;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.strategy.impl.KinopoiskSeriesGetStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SeriesGetStrategyContext {
    private final KinopoiskSeriesGetStrategy seriesGetStrategy;

    public SeriesDetailViewRs get(Map<ExternalId, String> externalIds) {
        String kinopoiskId = externalIds.get(ExternalId.KINOPOISK);
        if (kinopoiskId == null) {
            throw new IllegalArgumentException("Not found kinopoiskId");
        }
        return seriesGetStrategy.search(kinopoiskId);
    }
}
