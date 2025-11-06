package io.github.nikoir.seriesparser.dto.response.movielab.shorts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.seriesparser.dto.response.movielab.ContentType;
import io.github.nikoir.seriesparser.serialization.ContentTypeDeserializer;

import java.util.HashMap;

public record ContentItem(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("content_type")
        @JsonDeserialize(using = ContentTypeDeserializer.class)
        ContentType contentType,

        @JsonProperty("kp_id")
        Integer kpId,

        @JsonProperty("title")
        String title,

        @JsonProperty("orig_title")
        String origTitle,

        @JsonProperty("add")
        String add,

        @JsonProperty("year")
        String year,

        @JsonProperty("translations")
        HashMap<String, String> translations,

        @JsonProperty("imdb_id")
        String imdbId,

        @JsonProperty("iframe_src")
        String iframeSrc,

        @JsonProperty("iframe")
        String iframe
) {
}