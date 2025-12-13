package io.github.nikoir.series.tracker.service.search.series;

import io.github.nikoir.series.tracker.config.props.KinopoiskProps;
import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.dto.api.response.kinopoisk.KinopoiskSeriesSearchRs;
import io.github.nikoir.series.tracker.adapter.series.KinopoiskSeriesShortAdapter;
import io.github.nikoir.series.tracker.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KinopoiskSearchStrategy implements SeriesSearchStrategy {
    private final KinopoiskProps kinopoiskProps;

    private final KinopoiskSeriesShortAdapter seriesMapper;

    private final RestTemplate restTemplate;

    public PagedModel<SeriesShortViewRs> search(SeriesSearchRq request) {
        String token = kinopoiskProps.getCredentials().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriBuilder.from(kinopoiskProps.getUrl())
                .path(kinopoiskProps.getSeriesSearch().getPath())
                .param("page", request.page())
                .param("limit", request.limit())
                .param("query", request.title())
                .build();

        ResponseEntity<KinopoiskSeriesSearchRs> response = restTemplate.exchange(url, HttpMethod.GET, entity, KinopoiskSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }
}
