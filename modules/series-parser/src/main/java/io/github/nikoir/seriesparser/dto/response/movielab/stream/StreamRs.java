package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StreamRs(
        @JsonProperty("ads")
        Ad ads,

        @JsonProperty("player")
        Player player
) {}