package io.github.nikoir.seriesparser.service.series.content.create;

import io.github.nikoir.seriesparser.config.props.KinopoiskProps;
import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.domain.repo.SeriesRepository;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.KinopoiskSeriesInfoRs;
import io.github.nikoir.seriesparser.mapper.SeriesMapper;
import io.github.nikoir.seriesparser.service.series.content.sync.SeriesContentSyncService;
import io.github.nikoir.seriesparser.util.UriBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static io.github.nikoir.seriesparser.enums.ExternalId.KINOPOISK;

@Service
@RequiredArgsConstructor
@Transactional
public class KinopoiskContentCreateService implements SeriesContentCreateService {
    private final SeriesMapper seriesMapper;
    private final RestTemplate restTemplate;
    private final KinopoiskProps kinopoiskProps;
    private final SeriesRepository seriesRepository;
    private final SeriesContentSyncService seriesContentSyncService;

    @Override
    public void createSeries(Map<String, String> externalIds) {
        String kinopoiskId = externalIds.get(KINOPOISK.getSourceName());

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

        Series newSeries = seriesMapper.toEntity(response.getBody());
        seriesRepository.save(newSeries);

        seriesContentSyncService.syncSeriesContent(newSeries);
    }
}
