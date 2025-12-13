package io.github.nikoir.series.tracker.config.api;

import io.github.nikoir.series.tracker.config.props.LumexPortalProps;
import io.github.nikoir.series.tracker.config.props.LumexSiteProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LumexConfig {
    @Bean
    @ConfigurationProperties(prefix="api.lumex-site")
    public LumexSiteProps lumexSiteProps() {
        return new LumexSiteProps();
    }

    @Bean
    @ConfigurationProperties(prefix="api.lumex-portal")
    public LumexPortalProps lumexPortalProps() {
        return new LumexPortalProps();
    }
}
