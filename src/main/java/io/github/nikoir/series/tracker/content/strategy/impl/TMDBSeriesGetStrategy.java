package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.detail.TMDBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.service.RequestBuilder;
import io.github.nikoir.series.tracker.content.strategy.CountryGetStrategy;
import io.github.nikoir.series.tracker.content.strategy.SeriesGetStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TMDBSeriesGetStrategy implements SeriesGetStrategy {
    private final CountryGetStrategy countryGetStrategy;
    private final TMDBProps tmdbProps;
    private final RequestBuilder requestBuilder;
    private final TMDBSeriesDetailAdapter seriesDetailAdapter;
    private final RestTemplate restTemplate;

    @Override
    public Source getDataSource() {
        return Source.TMDB;
    }

    public Optional<SeriesDetailViewRs> get(Map<ExternalId, String> externalIds) {
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(tmdbProps.getCredentials(), "token");
        String tmdbId = externalIds.get(ExternalId.TMDB);
        if (StringUtils.isEmpty(tmdbId)) {
            throw new IllegalArgumentException("Not found tmdb id!"); //TODO: кастомные исключения
        }

        String url = UriComponentsBuilder.fromUriString(tmdbProps.getUrl())
                .path(tmdbProps.getSeriesDetails().getPath())
                .queryParam("language", "ru-RU")
                .build(false)
                .expand(tmdbId)
                .toUriString();

        ResponseEntity<TMDBSeriesInfoRs> response = restTemplate.exchange(url,
                HttpMethod.GET,
                authEntity,
                TMDBSeriesInfoRs.class);
        if (!response.hasBody()) {
            return Optional.empty();
        }

        TMDBSeriesInfoRs tmdbSeriesInfoRs = response.getBody();

        List<String> isoCodes = getProductionCountryIsoCodes(tmdbSeriesInfoRs);
        List<CountryRs> countryNames = countryGetStrategy.getCountriesByCodes(isoCodes);

        SeriesDetailViewRs seriesDetailViewRs = seriesDetailAdapter.toViewDto(tmdbSeriesInfoRs);
        seriesDetailViewRs.setCountries(countryNames);

        return Optional.of(seriesDetailViewRs);
    }

    private List<String> getProductionCountryIsoCodes(TMDBSeriesInfoRs tmdbSeriesInfoRs) {
        return tmdbSeriesInfoRs
                .productionCountries()
                .stream()
                .map(TMDBSeriesInfoRs.ProductionCountry::iso3166_1)
                .toList();
    }
}
