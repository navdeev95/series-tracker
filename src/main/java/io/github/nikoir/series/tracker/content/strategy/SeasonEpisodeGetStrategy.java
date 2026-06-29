package io.github.nikoir.series.tracker.content.strategy;

import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.enums.ExternalId;

import java.util.List;
import java.util.Map;

public interface SeasonEpisodeGetStrategy {
    List<SeasonInfo> getSeasonsWithEpisodes(Map<ExternalId, String> externalIds);
}
