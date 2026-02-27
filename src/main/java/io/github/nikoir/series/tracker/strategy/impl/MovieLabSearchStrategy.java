package io.github.nikoir.series.tracker.strategy.impl;

import io.github.nikoir.series.tracker.config.props.MovieLabProps;
import io.github.nikoir.series.tracker.dto.external.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.external.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.dto.integration.response.movielab.series.search.MovieLabSeriesSearchRs;
import io.github.nikoir.series.tracker.adapter.series.shorts.MovieLabSeriesShortAdapter;
import io.github.nikoir.series.tracker.enums.Source;
import io.github.nikoir.series.tracker.strategy.SeriesSearchStrategy;
import io.github.nikoir.series.tracker.util.UriBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MovieLabSearchStrategy implements SeriesSearchStrategy {
    private final MovieLabSeriesShortAdapter seriesMapper;

    private final HttpHeaders movieLabHeaders;

    private final MovieLabProps movieLabProps;

    private final RestTemplate restTemplate;

    @Override
    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String title = "";
        if (StringUtils.isNotEmpty(request.title())) {
            if (request.title().length() > 40) {
                title = request.title().substring(0, 40);
            } else {
                title = request.title();
            }
        }

        String url = UriBuilder.from(movieLabProps.getUrl())
                .path(movieLabProps.getSeriesSearch().getPath())
                .param("title", title)
                .param("page", request.page() + 1)
                .param("limit", request.limit())
                .disableEncoding()
                .build();

        ResponseEntity<MovieLabSeriesSearchRs> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, MovieLabSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }

    @Override
    public Source getDataSource() {
        return Source.MOVIELAB;
    }
}
