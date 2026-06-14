package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.detail.TMDBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.service.RequestBuilder;
import io.github.nikoir.series.tracker.content.strategy.SeriesGetStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TMDBSeriesGetStrategy implements SeriesGetStrategy {
    private final TMDBProps tmdbProps;
    private final RequestBuilder requestBuilder;
    private final TMDBSeriesDetailAdapter seriesDetailAdapter;
    private final RestTemplate restTemplate;

    @Override
    public Source getDataSource() {
        return Source.TMDB;
    }

    public SeriesDetailViewRs get(Map<ExternalId, String> externalIds) {
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(tmdbProps.getCredentials(), "token");
        String tmdbId = externalIds.get(ExternalId.TMDB);
        if (StringUtils.isEmpty(tmdbId)) {
            throw new IllegalArgumentException("Not found tmdb id!"); //TODO: кастомные исключения
        }

        String url = UriBuilder.from(tmdbProps.getUrl())
                .path(tmdbProps.getSeriesDetails().getPath())
                .var("series_id", tmdbId)
                .param("language", "ru-RU")
                .disableEncoding()
                .build();

        ResponseEntity<TMDBSeriesInfoRs> response = restTemplate.exchange(url,
                HttpMethod.GET,
                authEntity,
                TMDBSeriesInfoRs.class);

        return seriesDetailAdapter.toViewDto(response.getBody());
    }
}
