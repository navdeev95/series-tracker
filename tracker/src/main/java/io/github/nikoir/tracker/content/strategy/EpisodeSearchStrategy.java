package io.github.nikoir.tracker.content.strategy;

import io.github.nikoir.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.common.dto.response.ExternalId;

import java.util.List;
import java.util.Map;

public interface EpisodeSearchStrategy extends GettingStrategy {
    List<SeasonInfo> searchEpisodes(Map<ExternalId, String> externalIds);
}
