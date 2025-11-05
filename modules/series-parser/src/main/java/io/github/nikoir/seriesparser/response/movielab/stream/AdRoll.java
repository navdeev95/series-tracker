package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AdRoll {
    @JsonProperty("tag_url")
    private String tagUrl;

    @JsonProperty("time_offset")
    private String timeOffset;
}