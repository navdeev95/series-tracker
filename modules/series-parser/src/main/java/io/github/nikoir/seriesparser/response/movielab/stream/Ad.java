package io.github.nikoir.seriesparser.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Ad {
    @JsonProperty("rolls")
    private List<AdRoll> rolls;

    @JsonProperty("banners")
    private BannerSettings banners;}
