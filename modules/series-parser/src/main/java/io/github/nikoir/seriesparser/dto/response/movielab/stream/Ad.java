package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Ad(
        @JsonProperty("rolls")
        List<AdRoll> rolls,

        @JsonProperty("banners")
        BannerSettings banners
) {}