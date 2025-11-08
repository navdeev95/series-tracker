package io.github.nikoir.seriesparser.service.series.search;

import io.github.nikoir.seriesparser.config.MovieLabProperties;
import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.dto.response.movielab.search.MovieLabSearchRs;
import io.github.nikoir.seriesparser.mapper.SeriesMapper;
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
    private final SeriesMapper seriesMapper;

    private final HttpHeaders movieLabHeaders;

    private final MovieLabProperties movieLabProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PagedModel<SeriesViewRs> search(SeriesSearchRq request) {
        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String url = UriBuilder.from(movieLabProperties.getSearchUrl())
                .param("title", request.title())
                .param("page", request.page())
                .param("limit", request.limit())
                .build();

        ResponseEntity<MovieLabSearchRs> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, MovieLabSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
