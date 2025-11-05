package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BannerSettings {
    @JsonProperty("pausebanner")
    private Boolean pauseBanner;

    @JsonProperty("endtag")
    private Boolean endTag;
}
