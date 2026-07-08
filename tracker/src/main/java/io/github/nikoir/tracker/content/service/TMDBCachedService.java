package io.github.nikoir.tracker.content.service;

import io.github.nikoir.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.tracker.content.dto.integration.TMDBSeriesInfoRs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TMDBCachedService {
    private final TMDBProps tmdbProps;
    private final RequestBuilder requestBuilder;
    private final RestTemplate restTemplate;

    public Optional<TMDBSeriesInfoRs> getSeriesInfo(String tmdbId) {
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(tmdbProps.getCredentials(), "token");
        String url = UriComponentsBuilder.fromUriString(tmdbProps.getUrl())
                .path(tmdbProps.getSeriesDetails().getPath())
                .queryParam("language", "ru-RU")
                .build(false)
                .expand(tmdbId)
                .toUriString();

        ResponseEntity<TMDBSeriesInfoRs> response = restTemplate.exchange(url,
                HttpMethod.GET,
                authEntity,
                TMDBSeriesInfoRs.class);
        if (!response.hasBody() || !response.getStatusCode().is2xxSuccessful()) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }
}
