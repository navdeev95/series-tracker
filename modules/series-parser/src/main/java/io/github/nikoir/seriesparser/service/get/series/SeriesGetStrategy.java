package io.github.nikoir.seriesparser.service.get.series;

import io.github.nikoir.seriesparser.dto.internal.SeriesDetailViewRs;

public interface SeriesGetStrategy {
    SeriesDetailViewRs get(String externalId);
}
