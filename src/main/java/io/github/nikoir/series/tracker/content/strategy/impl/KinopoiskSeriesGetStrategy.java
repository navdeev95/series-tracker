package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.adapter.series.detail.SeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.config.props.KinopoiskProps;
import io.github.nikoir.series.tracker.content.dto.integration.response.kinopoisk.KinopoiskSeriesInfoRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.SeriesGetStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KinopoiskSeriesGetStrategy implements SeriesGetStrategy {
    private final SeriesDetailAdapter<KinopoiskSeriesInfoRs> seriesDetailAdapter;
    private final RestTemplate restTemplate;
    private final KinopoiskProps kinopoiskProps;

    @Override
    public SeriesDetailViewRs search(String kinopoiskId) {
        String token = kinopoiskProps.getCredentials().get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriBuilder.from(kinopoiskProps.getUrl())
                .path(kinopoiskProps.getSeriesInfo().getPath())
                .path(kinopoiskId)
                .build();

        ResponseEntity<KinopoiskSeriesInfoRs> response = restTemplate.exchange(url, HttpMethod.GET, entity, KinopoiskSeriesInfoRs.class);
        return seriesDetailAdapter.toViewDto(response.getBody());
    }

    @Override
    public Source getDataSource() {
        return Source.KINOPOISK;
    }
}
