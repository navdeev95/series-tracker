package io.github.nikoir.tracker.content.config.api.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "api.lumex-portal")
public class LumexPortalProps {
    String url;
    MethodProps shortInfo;
    Map<String, String> credentials;
}
