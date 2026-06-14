package io.github.nikoir.series.tracker.content.config.api.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.wikidata")
@Component
public class WikidataProps {
    private String url;
    private MethodProps getEntity;
    private Map<String, String> credentials;
}
