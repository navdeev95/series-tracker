package io.github.nikoir.series.tracker.content.config.api.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.tmdb")
public class TMDBProps {
    private String url;
    private MethodProps auth;
    private MethodProps seriesSearch;
    private MethodProps seriesDetails;
    private MethodProps externalIds;
    private MethodProps countries;
    private Map<String, String> credentials;
}
