package io.github.nikoir.series.tracker.content.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.lumex-site")
public class LumexSiteProps {
    String url;
    MethodProps login;
    MethodProps streamInfo;
    Map<String, String> credentials;
}
