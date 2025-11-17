package io.github.nikoir.seriesparser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KinopoiskConfig {
    @Bean
    @ConfigurationProperties(prefix="api.kinopoisk")
    public KinopoiskProperties kinopoiskProperties() {
        return new KinopoiskProperties();
    }
}
