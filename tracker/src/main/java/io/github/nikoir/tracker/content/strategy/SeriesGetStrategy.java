package io.github.nikoir.tracker.content.strategy;

import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.common.dto.response.ExternalId;

import java.util.Map;
import java.util.Optional;

public interface SeriesGetStrategy extends GettingStrategy {
    Optional<SeriesDetailViewRs> get(Map<ExternalId, String> externalIds);
}
