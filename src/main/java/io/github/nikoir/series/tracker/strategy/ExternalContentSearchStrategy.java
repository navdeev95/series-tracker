package io.github.nikoir.series.tracker.strategy;

import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import io.github.nikoir.series.tracker.enums.ExternalSource;

import java.util.List;
import java.util.Map;

public interface ExternalContentSearchStrategy {
    List<SeasonViewRs> search(Map<String, String> externalIds);
    ExternalSource getSource();
}
