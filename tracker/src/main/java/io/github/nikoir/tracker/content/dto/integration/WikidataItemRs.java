package io.github.nikoir.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record WikidataItemRs(
        @JsonProperty("id")
        String id,

        @JsonProperty("type")
        String type,

        @JsonProperty("labels")
        Map<String, String> labels,

        @JsonProperty("descriptions")
        Map<String, String> descriptions,

        @JsonProperty("aliases")
        Map<String, List<String>> aliases,

        @JsonProperty("statements")
        Map<String, List<WikidataStatement>> statements,

        @JsonProperty("sitelinks")
        Map<String, WikidataSitelink> sitelinks
) {
    public record WikidataStatement(
            @JsonProperty("id")
            String id,

            @JsonProperty("rank")
            String rank,

            @JsonProperty("value")
            WikidataStatementValue value
    ) {}

    public record WikidataStatementValue(
            @JsonProperty("type")
            String type,

            @JsonProperty("content")
            Object content
    ) {}

    public record WikidataSitelink(
            @JsonProperty("title")
            String title,

            @JsonProperty("url")
            String url
    ) {}
}
