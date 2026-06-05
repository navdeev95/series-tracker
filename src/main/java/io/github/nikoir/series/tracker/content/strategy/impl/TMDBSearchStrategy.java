package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.shorts.TMDBSeriesShortAdapter;
import io.github.nikoir.series.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.series.tracker.content.dto.integration.response.tmdb.TMDBSeriesSearchRs;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.SeriesSearchStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TMDBSearchStrategy implements SeriesSearchStrategy {
    private final TMDBProps tmdbProps;

    private final RestTemplate restTemplate;

    private final TMDBSeriesShortAdapter seriesMapper;

    @Override
    public Source getDataSource() {
        return Source.TMDB;
    }

    @Override
    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        String token = tmdbProps.getCredentials().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriBuilder.from(tmdbProps.getUrl())
                .path(tmdbProps.getSeriesSearch().getPath())
                .param("page", request.page() + 1)
                .param("include_adult", true)
                .param("query", request.title())
                .param("language", "ru-RU")
                .disableEncoding()
                .build();

        ResponseEntity<TMDBSeriesSearchRs> response = restTemplate.exchange(url, HttpMethod.GET, entity, TMDBSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
