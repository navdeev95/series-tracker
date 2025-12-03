package io.github.nikoir.seriesparser.service.search.content.strategy;

import io.github.nikoir.seriesparser.dto.internal.SeasonViewRs;
import io.github.nikoir.seriesparser.enums.ExternalSource;

import java.util.List;
import java.util.Map;

public interface ExternalContentSearchStrategy {
    List<SeasonViewRs> search(Map<String, String> externalIds);
    ExternalSource getSource();
}
