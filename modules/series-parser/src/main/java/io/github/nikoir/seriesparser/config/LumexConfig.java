package io.github.nikoir.seriesparser.config;

import io.github.nikoir.seriesparser.config.props.LumexPortalProps;
import io.github.nikoir.seriesparser.config.props.LumexSiteProps;
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
