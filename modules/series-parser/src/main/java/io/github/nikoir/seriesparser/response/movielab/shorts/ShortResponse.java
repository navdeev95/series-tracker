package io.github.nikoir.seriesparser.response.movielab.shorts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShortResponse {
    @JsonProperty("result")
    private Boolean result;

    @JsonProperty("php")
    private Double php;

    @JsonProperty("data")
    private List<ContentItem> data;
}
