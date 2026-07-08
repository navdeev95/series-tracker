package io.github.nikoir.tracker.content.strategy.impl;

import io.github.nikoir.common.dto.request.SeriesSearchRq;
import io.github.nikoir.common.dto.response.SeriesListViewRs;
import io.github.nikoir.tracker.content.adapter.series.shorts.TMDBSeriesShortAdapter;
import io.github.nikoir.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.tracker.content.dto.integration.TMDBSeriesSearchRs;
import io.github.nikoir.tracker.content.enums.Source;
import io.github.nikoir.tracker.content.service.RequestBuilder;
import io.github.nikoir.tracker.content.strategy.SeriesSearchStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TMDBSeriesSearchStrategy implements SeriesSearchStrategy {
    private final RequestBuilder requestBuilder;
    private final RestTemplate restTemplate;
    private final TMDBSeriesShortAdapter seriesMapper;
    private final TMDBProps tmdbProps;


    @Override
    public Source getDataSource() {
        return Source.TMDB;
    }

    @Override
    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(tmdbProps.getCredentials(), "token");

        String url = UriComponentsBuilder.fromUriString(tmdbProps.getUrl())
                .path(tmdbProps.getSeriesSearch().getPath())
                .queryParam("page", request.page() + 1)
                .queryParam("include_adult", true)
                .queryParam("query", request.title())
                .queryParam("language", "ru-RU")
                .build(false)
                .toUriString();

        ResponseEntity<TMDBSeriesSearchRs> response = restTemplate.exchange(url, HttpMethod.GET, authEntity, TMDBSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
