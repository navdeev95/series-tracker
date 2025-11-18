package io.github.nikoir.seriesparser.config;

import io.github.nikoir.seriesparser.config.props.KinopoiskProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KinopoiskConfig {
    @Bean
    @ConfigurationProperties(prefix="api.kinopoisk")
    public KinopoiskProps kinopoiskProperties() {
        return new KinopoiskProps();
    }
}
