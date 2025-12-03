package io.github.nikoir.seriesparser.dto.api.request.movielab;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovieLabLoginRq(
        @JsonProperty("username")
        String username,

        @JsonProperty("password")
        String password
) {
}