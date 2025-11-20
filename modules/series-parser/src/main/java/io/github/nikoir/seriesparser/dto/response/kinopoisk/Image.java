package io.github.nikoir.seriesparser.dto.response.kinopoisk;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Image(@JsonProperty("url")
                    String url,

                    @JsonProperty("previewUrl")
                    String previewUrl) {
}
