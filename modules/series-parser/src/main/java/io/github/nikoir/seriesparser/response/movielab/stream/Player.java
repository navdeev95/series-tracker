package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.seriesparser.response.movielab.ContentType;
import io.github.nikoir.seriesparser.serialization.ContentTypeDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Player {
    @JsonProperty("content_type")
    @JsonDeserialize(using = ContentTypeDeserializer.class)
    private ContentType contentType;

    @JsonProperty("content_id")
    private Integer contentId;

    @JsonProperty("kinopoisk_id")
    private Integer kinopoiskId;

    @JsonProperty("poster")
    private String poster;

    @JsonProperty("media")
    private List<Season> media;
}
