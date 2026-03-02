package io.github.nikoir.series.tracker.content.config.props;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.movielab")
public class MovieLabProps {
    private String url;
    private MethodProps seriesSearch;
    private MethodProps episodeSearch;
    private Map<String, String> credentials;
}
