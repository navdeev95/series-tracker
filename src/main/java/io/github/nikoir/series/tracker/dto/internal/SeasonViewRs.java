package io.github.nikoir.series.tracker.dto.internal;

import io.github.nikoir.series.tracker.enums.ExternalSource;

import java.util.Date;
import java.util.List;

public record SeasonViewRs(String name,
                           Integer number,
                           Date releaseDate,
                           ExternalSource externalSource,
                           Integer episodesCount,
                           List<EpisodeViewRs> episodes) {

    public record EpisodeViewRs(String name,
                                Integer number,
                                List<String> translations,
                                Date releaseDate) {
    }
}
