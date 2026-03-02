package io.github.nikoir.series.tracker.content.config.api;

import io.github.nikoir.series.tracker.content.config.api.props.KinopoiskProps;
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
