package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.seriesparser.dto.response.movielab.ContentType;
import io.github.nikoir.seriesparser.serialization.ContentTypeDeserializer;
import java.util.List;

public record Player(
        @JsonProperty("content_type")
        @JsonDeserialize(using = ContentTypeDeserializer.class)
        ContentType contentType,

        @JsonProperty("content_id")
        Integer contentId,

        @JsonProperty("kinopoisk_id")
        Integer kinopoiskId,

        @JsonProperty("poster")
        String poster,

        @JsonProperty("media")
        List<Season> media
) {}