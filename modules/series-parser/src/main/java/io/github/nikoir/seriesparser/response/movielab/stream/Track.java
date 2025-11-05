package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Track {
    @JsonProperty("kind")
    private String kind;

    @JsonProperty("src")
    private String src;

    @JsonProperty("srlang")
    private String language;

    @JsonProperty("label")
    private String label;
}
