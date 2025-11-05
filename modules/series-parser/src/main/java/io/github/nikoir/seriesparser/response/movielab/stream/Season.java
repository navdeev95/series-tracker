package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Season {
    @JsonProperty("season_id")
    private Integer seasonId;

    @JsonProperty("season_name")
    private String seasonName;

    @JsonProperty("episodes")
    private List<Episode> episodes;

}
