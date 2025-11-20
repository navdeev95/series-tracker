package io.github.nikoir.seriesparser.dto.response;

import java.util.Date;
import java.util.List;

public record SeasonViewRs(String name,
                           Integer number,
                           Date releaseDate,
                           Integer episodesCount,
                           List<EpisodeViewRs> episodes) {

    public record EpisodeViewRs(String name,
                                Integer number,
                                List<String> translations,
                                Date releaseDate) {
    }
}
