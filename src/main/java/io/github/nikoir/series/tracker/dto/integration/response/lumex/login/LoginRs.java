package io.github.nikoir.series.tracker.dto.integration.response.lumex.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRs(
        @JsonProperty("accessToken")
        String accessToken,

        @JsonProperty("refreshToken")
        String refreshToken
) {}