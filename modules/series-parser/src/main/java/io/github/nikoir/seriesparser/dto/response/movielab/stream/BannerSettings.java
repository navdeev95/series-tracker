package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BannerSettings(
        @JsonProperty("pausebanner")
        Boolean pauseBanner,

        @JsonProperty("endtag")
        Boolean endTag
) {}