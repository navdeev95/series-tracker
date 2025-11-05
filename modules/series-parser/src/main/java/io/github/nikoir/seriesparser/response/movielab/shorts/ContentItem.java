package io.github.nikoir.seriesparser.response.movielab.shorts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.seriesparser.response.movielab.ContentType;
import io.github.nikoir.seriesparser.serialization.ContentTypeDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
@NoArgsConstructor
@Data
public class ContentItem {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("content_type")
    @JsonDeserialize(using = ContentTypeDeserializer.class)
    private ContentType contentType;

    @JsonProperty("kp_id")
    private Integer kpId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("orig_title")
    private String origTitle;

    @JsonProperty("add")
    private String add;

    @JsonProperty("year")
    private String year;

    @JsonProperty("translations")
    private HashMap<String, String> translations;

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("iframe_src")
    private String iframeSrc;

    @JsonProperty("iframe")
    private String iframe;

}
