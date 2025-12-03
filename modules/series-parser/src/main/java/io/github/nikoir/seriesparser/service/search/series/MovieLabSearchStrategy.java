package io.github.nikoir.seriesparser.service.search.series;

import io.github.nikoir.seriesparser.config.props.MovieLabProps;
import io.github.nikoir.seriesparser.dto.api.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.internal.SeriesShortViewRs;
import io.github.nikoir.seriesparser.dto.api.response.movielab.series.search.MovieLabSeriesSearchRs;
import io.github.nikoir.seriesparser.adapter.series.MovieLabSeriesShortAdapter;
import io.github.nikoir.seriesparser.util.UriBuilder;
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
    public PagedModel<SeriesShortViewRs> search(SeriesSearchRq request) {
        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String url = UriBuilder.from(movieLabProps.getUrl())
                .path(movieLabProps.getSeriesSearch().getPath())
                .param("title", request.title())
                .param("page", request.page())
                .param("limit", request.limit())
                .disableEncoding()
                .build();

        ResponseEntity<MovieLabSeriesSearchRs> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, MovieLabSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
