package io.github.nikoir.series.tracker.content.strategy;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;

import java.util.List;
import java.util.Map;

public interface EpisodeSearchStrategy extends GettingStrategy {
    List<SeasonViewRs> searchEpisodes(Map<ExternalId, String> externalIds);
}
