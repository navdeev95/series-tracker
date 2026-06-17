package io.github.nikoir.series.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TMDBCountryRs(
        @JsonProperty("iso_3166_1")
        String iso3166_1,

        @JsonProperty("english_name")
        String englishName,

        @JsonProperty("native_name")
        String nativeName
) {
}
