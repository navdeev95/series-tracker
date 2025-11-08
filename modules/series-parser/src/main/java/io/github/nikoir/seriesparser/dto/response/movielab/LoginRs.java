package io.github.nikoir.seriesparser.dto.response.movielab;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRs(
        @JsonProperty("accessToken")
        String accessToken,

        @JsonProperty("refreshToken")
        String refreshToken
) {}