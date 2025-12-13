package io.github.nikoir.series.tracker.service.get.series;

import io.github.nikoir.series.tracker.dto.internal.SeriesDetailViewRs;

public interface SeriesGetStrategy {
    SeriesDetailViewRs get(String externalId);
}
