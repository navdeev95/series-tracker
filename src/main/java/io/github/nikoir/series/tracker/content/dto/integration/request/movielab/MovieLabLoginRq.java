package io.github.nikoir.series.tracker.content.dto.integration.request.movielab;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovieLabLoginRq(
        @JsonProperty("username")
        String username,

        @JsonProperty("password")
        String password
) {
}