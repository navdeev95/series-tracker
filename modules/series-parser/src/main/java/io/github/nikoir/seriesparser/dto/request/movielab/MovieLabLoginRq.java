package io.github.nikoir.seriesparser.dto.request.movielab;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovieLabLoginRq(
        @JsonProperty("username")
        String username,

        @JsonProperty("password")
        String password
) {
}