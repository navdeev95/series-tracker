package io.github.nikoir.series.tracker.content.config.api.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.wikidata")
public class WikidataProps {
    private String url;
    private MethodProps getEntity;
    private Map<String, String> credentials;
}
