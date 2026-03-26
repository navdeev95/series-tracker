package io.github.nikoir.series.tracker.common.dto.response;

import io.github.nikoir.series.tracker.content.enums.Source;

import java.util.Date;
import java.util.List;

public record SeasonViewRs(String name,
                           Integer number,
                           Date releaseDate,
                           //Source externalSource,
                           //Integer episodesCount,
                           List<EpisodeViewRs> episodes) {

    public record EpisodeViewRs(String name,
                                Integer number,
                                List<String> translations,
                                Date releaseDate) {
    }
}
