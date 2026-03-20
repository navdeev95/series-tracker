package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.config.api.props.KinopoiskProps;
import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.dto.integration.response.kinopoisk.KinopoiskSeriesSearchRs;
import io.github.nikoir.series.tracker.content.adapter.series.shorts.KinopoiskSeriesShortAdapter;
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
public class KinopoiskSearchStrategy implements SeriesSearchStrategy {
    private final KinopoiskProps kinopoiskProps;

    private final KinopoiskSeriesShortAdapter seriesMapper;

    private final RestTemplate restTemplate;

    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        String token = kinopoiskProps.getCredentials().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriBuilder.from(kinopoiskProps.getUrl())
                .path(kinopoiskProps.getSeriesSearch().getPath())
                .param("page", request.page() + 1)
                .param("limit", request.limit())
                .param("query", request.title())
                .disableEncoding()
                .build();

        ResponseEntity<KinopoiskSeriesSearchRs> response = restTemplate.exchange(url, HttpMethod.GET, entity, KinopoiskSeriesSearchRs.class);

        return seriesMapper.toViewDtoPage(response.getBody());
    }

    @Override
    public Source getDataSource() {
        return Source.KINOPOISK;
    }
}
