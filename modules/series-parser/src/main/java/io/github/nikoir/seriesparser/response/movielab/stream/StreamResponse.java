package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StreamResponse {
    @JsonProperty("ads")
    private Ad ads;

    @JsonProperty("player")
    private Player player;
}
