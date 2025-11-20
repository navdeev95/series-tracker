package io.github.nikoir.seriesparser.service.episode.search;

import io.github.nikoir.seriesparser.dto.response.SeasonViewRs;

import java.util.List;
import java.util.Map;

public interface ExternalContentSearchStrategy {
    List<SeasonViewRs> search(Map<String, String> externalIds);
}
