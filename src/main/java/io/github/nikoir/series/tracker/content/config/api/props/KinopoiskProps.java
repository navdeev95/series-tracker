package io.github.nikoir.series.tracker.content.config.api.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.kinopoisk")
public class KinopoiskProps{
    private String url;
    private MethodProps seriesSearch;
    private MethodProps seriesInfo;
    private Map<String, String> credentials;
}
