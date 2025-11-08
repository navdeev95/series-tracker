package io.github.nikoir.seriesparser.dto.response.kinopoisk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NameSearchRs(
        @JsonProperty("docs")
        List<Doc> docs,

        @JsonProperty("total")
        Integer total,

        @JsonProperty("page")
        Integer page,

        @JsonProperty("pages")
        Integer pages,

        @JsonProperty("limit")
        Integer limit
) {

}