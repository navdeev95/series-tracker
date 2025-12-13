package io.github.nikoir.series.tracker.dto.internal;

import java.util.Map;

public record SeriesShortViewRs(String title,
                                Integer year,
                                String posterUrl,
                                Integer totalSeasons,
                                Map<String, String> externalIds,
                                boolean isSeries) {
}
