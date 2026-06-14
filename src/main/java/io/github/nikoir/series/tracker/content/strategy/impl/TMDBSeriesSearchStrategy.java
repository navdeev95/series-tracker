package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.shorts.TMDBSeriesShortAdapter;
import io.github.nikoir.series.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesSearchRs;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.service.RequestBuilder;
import io.github.nikoir.series.tracker.content.strategy.SeriesSearchStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        String url = UriBuilder.from(tmdbProps.getUrl())
                .path(tmdbProps.getSeriesSearch().getPath())
                .param("page", request.page() + 1)
                .param("include_adult", true)
                .param("query", request.title())
                .param("language", "ru-RU")
                .disableEncoding()
                .build();

        ResponseEntity<TMDBSeriesSearchRs> response = restTemplate.exchange(url, HttpMethod.GET, authEntity, TMDBSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
