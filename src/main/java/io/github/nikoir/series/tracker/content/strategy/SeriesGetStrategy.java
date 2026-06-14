package io.github.nikoir.series.tracker.content.strategy;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;

import java.util.Map;

public interface SeriesGetStrategy extends GettingStrategy {
    //TODO: сделать optional
    SeriesDetailViewRs get(Map<ExternalId, String> externalIds);
}
