package io.github.nikoir.seriesparser.service.get.series;

import io.github.nikoir.seriesparser.adapter.series.SeriesDetailAdapter;
import io.github.nikoir.seriesparser.adapter.series.SeriesShortAdapter;
import io.github.nikoir.seriesparser.config.props.KinopoiskProps;
import io.github.nikoir.seriesparser.dto.api.response.kinopoisk.KinopoiskSeriesInfoRs;
import io.github.nikoir.seriesparser.dto.internal.SeriesDetailViewRs;
import io.github.nikoir.seriesparser.util.UriBuilder;
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
    public SeriesDetailViewRs get(String kinopoiskId) {
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
}
