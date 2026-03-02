package io.github.nikoir.series.tracker.content.config.api;

import io.github.nikoir.series.tracker.content.config.logging.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {
    @Value("${api.connect-timeout:30000}")
    private Integer connectTimeout;

    @Value("${api.read-timeout:30000}")
    private Integer readTimeout;


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .readTimeout(Duration.ofMillis(readTimeout))
                .additionalInterceptors(Collections.singletonList(new LoggingInterceptor()))
                .build();
    }
}
