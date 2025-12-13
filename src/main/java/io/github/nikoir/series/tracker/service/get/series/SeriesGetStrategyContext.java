package io.github.nikoir.series.tracker.service.get.series;

import io.github.nikoir.series.tracker.enums.ExternalSource;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static io.github.nikoir.series.tracker.enums.ExternalSource.KINOPOISK;

@Component
public class SeriesGetStrategyContext {
    private final Map<ExternalSource, SeriesGetStrategy> strategies = new EnumMap<>(ExternalSource.class);

    public SeriesGetStrategyContext(SeriesGetStrategy kinopoiskStrategy)
    {
        strategies.put(KINOPOISK, kinopoiskStrategy);
    }

    public SeriesGetStrategy getStrategy(ExternalSource source) {
        SeriesGetStrategy strategy = strategies.get(source);
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy not found for: " + source);
        }
        return strategy;
    }
}
