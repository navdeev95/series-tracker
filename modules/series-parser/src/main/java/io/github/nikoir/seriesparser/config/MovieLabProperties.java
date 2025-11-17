package io.github.nikoir.seriesparser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api.movielab")
public class MovieLabProperties {
    private String username;
    private String password;
    private Root root;
    private Search search;
    private Search episodeSearch;
    private Login login;
    private ShortInfo shortInfo;
    private StreamInfo streamInfo;

    @Data
    public static class Root {
        private String url;
    }

    @Data
    public static class Search {
        private String url;
    }

    @Data
    public static class Login {
        private String url;
    }

    @Data
    public static class ShortInfo {
        private String url;
        private String token;
    }

    @Data
    public static class StreamInfo {
        private String url;
        private String clientId;
    }
}
