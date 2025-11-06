package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Episode(
        @JsonProperty("episode_id")
        Integer episodeId,

        @JsonProperty("name")
        String name,

        @JsonProperty("poster")
        String poster,

        @JsonProperty("media")
        List<Translation> media
) {}