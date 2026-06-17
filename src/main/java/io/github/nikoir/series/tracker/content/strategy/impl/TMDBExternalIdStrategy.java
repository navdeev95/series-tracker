package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBExternalIdsRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.service.RequestBuilder;
import io.github.nikoir.series.tracker.content.strategy.ExternalIdStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TMDBExternalIdStrategy implements ExternalIdStrategy {
    private final RequestBuilder requestBuilder;
    private final TMDBProps tmdbProps;
    private final RestTemplate restTemplate;

    @Override
    public Map<ExternalId, String> enrichExternalIds(Map<ExternalId, String> externalIds) {
        Map<ExternalId, String> result = new HashMap<>(externalIds);
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(tmdbProps.getCredentials(), "token");
        String tmdbId = externalIds.get(ExternalId.TMDB);
        if (StringUtils.isEmpty(tmdbId)) {
            throw new IllegalArgumentException("Not found tmdb id!"); //TODO: кастомные исключения
        }

        String url = UriBuilder.from(tmdbProps.getUrl())
                .path(tmdbProps.getExternalIds().getPath())
                .var("series_id", tmdbId)
                .disableEncoding()
                .build();

        ResponseEntity<TMDBExternalIdsRs> response = restTemplate.exchange(url,
                HttpMethod.GET,
                authEntity,
                TMDBExternalIdsRs.class);

        if (response.hasBody()) {
            TMDBExternalIdsRs responseBody = response.getBody();

            putIfNotBlankAndNotContains(result, ExternalId.IMDB, responseBody.imdbId());
            putIfNotBlankAndNotContains(result, ExternalId.WIKIDATA, responseBody.wikidataId());
        }

        return result;
    }

    @Override
    public Source getDataSource() {
        return Source.TMDB;
    }
}
