package io.github.nikoir.seriesparser.service.series.search;

import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.NameSearchRs;
import io.github.nikoir.seriesparser.mapper.SeriesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KinopoiskSearchStrategy implements SeriesSearchStrategy {
    @Value("${api.kinopoisk.root-url}")
    private String rootUrl;

    @Value("${api.kinopoisk.token}")
    private String token;

    @Value("${api.kinopoisk.name-search-path}")
    private String nameSearchPath;

    private final SeriesMapper seriesMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    public PagedModel<SeriesViewRs> search(SeriesSearchRq request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder;
        try {
            builder = UriComponentsBuilder
                    .fromUri(new URL(new URL(rootUrl), nameSearchPath).toURI())
                    .queryParam("page", 1)
                    .queryParam("limit", 10)
                    .queryParam("query", request.title());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<NameSearchRs> response = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, entity, NameSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
