package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Translation(
        @JsonProperty("translation_id")
        Integer translationId,

        @JsonProperty("translation_name")
        String translationName,

        @JsonProperty("playlist")
        String playlist,

        @JsonProperty("tracks")
        List<Track> tracks,

        @JsonProperty("max_quality")
        Integer maxQuality
) {}