package io.github.nikoir.tracker.content.config.api.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.tmdb")
public class TMDBProps {
    private String url;
    private MethodProps auth;
    private MethodProps seriesSearch;
    private MethodProps seriesDetails;
    private MethodProps externalIds;
    private MethodProps seasonDetails;
    private Map<String, String> credentials;
}
