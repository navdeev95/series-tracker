package io.github.nikoir.series.tracker.content.dto.integration.response.kinopoisk;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Image(@JsonProperty("url")
                    String url,

                    @JsonProperty("previewUrl")
                    String previewUrl) {
}
