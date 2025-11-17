package io.github.nikoir.seriesparser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api.kinopoisk")
public class KinopoiskProperties {
    private String rootUrl;
    private String token;
    private String nameSearchPath;
}
