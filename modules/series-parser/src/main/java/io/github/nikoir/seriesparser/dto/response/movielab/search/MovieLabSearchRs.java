package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MovieLabSearchRs(
        @JsonProperty("results") List<SearchResult> results,
        @JsonProperty("pagination") Pagination pagination
) {}

