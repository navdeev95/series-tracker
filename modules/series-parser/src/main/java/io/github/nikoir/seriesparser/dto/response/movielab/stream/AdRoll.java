package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AdRoll(
        @JsonProperty("tag_url")
        String tagUrl,

        @JsonProperty("time_offset")
        String timeOffset
) {}