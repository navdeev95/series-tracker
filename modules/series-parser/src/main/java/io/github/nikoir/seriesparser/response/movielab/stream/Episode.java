package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Episode {
    @JsonProperty("episode_id")
    private Integer episodeId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("poster")
    private String poster;

    @JsonProperty("media")
    private
    List<Translation> media;
}
