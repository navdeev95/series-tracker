package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Translation {
    @JsonProperty("translation_id")
    private Integer translationId;

    @JsonProperty("translation_name")
    private String translationName;

    @JsonProperty("playlist")
    private String playlist;

    @JsonProperty("tracks")
    private List<Track> tracks;

    @JsonProperty("max_quality")
    private Integer maxQuality;
}
