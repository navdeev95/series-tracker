package io.github.nikoir.series.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBSeasonInfoRs(
        @JsonProperty("_id")
        String id,

        @JsonProperty("air_date")
        String airDate,

        @JsonProperty("episodes")
        List<TMDBEpisodeInfoRs> episodes
) {
}
