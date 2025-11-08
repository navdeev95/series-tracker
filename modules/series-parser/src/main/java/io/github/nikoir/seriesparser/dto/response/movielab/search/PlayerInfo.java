package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerInfo(
        @JsonProperty("token") String token,
        @JsonProperty("iframe_url") String iframeUrl,
        @JsonProperty("block") String block,
        @JsonProperty("translator") String translator,
        @JsonProperty("translator_id") Integer translatorId,
        @JsonProperty("quality") String quality
) {}
