package io.github.nikoir.seriesparser.service.series.search;

import io.github.nikoir.seriesparser.config.KinopoiskProperties;
import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.NameSearchRs;
import io.github.nikoir.seriesparser.mapper.KinopoiskSeriesMapper;
import io.github.nikoir.seriesparser.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KinopoiskSearchStrategy implements SeriesSearchStrategy {
    private final KinopoiskProperties kinopoiskProperties;

    private final KinopoiskSeriesMapper seriesMapper;

    private final RestTemplate restTemplate;

    public PagedModel<SeriesViewRs> search(SeriesSearchRq request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", kinopoiskProperties.getToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriBuilder.from(kinopoiskProperties.getRootUrl())
                .path(kinopoiskProperties.getNameSearchPath())
                .param("page", request.page())
                .param("limit", request.limit())
                .param("query", request.title())
                .build();

        ResponseEntity<NameSearchRs> response = restTemplate.exchange(url, HttpMethod.GET, entity, NameSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
