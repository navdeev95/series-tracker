package io.github.nikoir.seriesparser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api.movielab")
public class MovieLabProperties {
    private String rootUrl;
    private String loginUrl;
    private String searchUrl;
    private String shortInfoUrl;
    private String streamInfoUrl;
    private String username;
    private String password;
    private String shortInfoToken;
    private String streamInfoClientId;
}
