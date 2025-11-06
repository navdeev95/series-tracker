package io.github.nikoir.seriesparser.dto.response.movielab.shorts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ShortResponse(
        @JsonProperty("result")
        Boolean result,

        @JsonProperty("php")
        Double php,

        @JsonProperty("data")
        List<ContentItem> data
) {}