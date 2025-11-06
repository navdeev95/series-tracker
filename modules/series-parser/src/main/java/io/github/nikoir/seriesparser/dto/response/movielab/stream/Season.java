package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Season(
        @JsonProperty("season_id")
        Integer seasonId,

        @JsonProperty("season_name")
        String seasonName,

        @JsonProperty("episodes")
        List<Episode> episodes
) {}