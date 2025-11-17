package io.github.nikoir.seriesparser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;

@Configuration
public class MovieLabConfig {

    @Bean
    @ConfigurationProperties(prefix = "api.movielab")
    public MovieLabProperties movieLabProperties() {
        return new MovieLabProperties();
    }

    @Bean
    public HttpHeaders movieLabBaseHeaders(
            MovieLabProperties properties
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setAcceptLanguage(List.of(new Locale.LanguageRange("ru")));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin(properties.getRoot().getUrl());
        headers.set("priority", "u=1, i");
        headers.set("referer", properties.getRoot().getUrl());
        headers.set("sec-ch-ua", "\"Microsoft Edge\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
        headers.set("sec-ch-ua-mobile", "?0");
        headers.set("sec-ch-ua-platform", "\"Windows\"");
        headers.set("sec-fetch-dest", "empty");
        headers.set("sec-fetch-mode", "cors");
        headers.set("sec-fetch-site", "cross-site");
        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0");
        return headers;
    }
}