package io.github.nikoir.tracker.content.strategy.impl;

import io.github.nikoir.common.dto.response.CountryRs;
import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.tracker.content.adapter.series.detail.TMDBSeriesDetailAdapter;
import io.github.nikoir.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.common.dto.response.ExternalId;
import io.github.nikoir.tracker.content.enums.Source;
import io.github.nikoir.tracker.content.service.TMDBCachedService;
import io.github.nikoir.tracker.content.strategy.CountryGetStrategy;
import io.github.nikoir.tracker.content.strategy.SeriesGetStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TMDBSeriesGetStrategy implements SeriesGetStrategy {
    private final CountryGetStrategy countryGetStrategy;
    private final TMDBSeriesDetailAdapter seriesDetailAdapter;
    private final TMDBCachedService tmdbCachedService;

    @Override
    public Source getDataSource() {
        return Source.TMDB;
    }

    public Optional<SeriesDetailViewRs> get(Map<ExternalId, String> externalIds) {
        String tmdbId = externalIds.get(ExternalId.TMDB);
        if (StringUtils.isEmpty(tmdbId)) {
            throw new IllegalArgumentException("Not found tmdb id!"); //TODO: кастомные исключения
        }

        Optional<TMDBSeriesInfoRs> tmdbSeriesInfoRs = tmdbCachedService.getSeriesInfo(tmdbId);
        if (tmdbSeriesInfoRs.isEmpty()) {
            return Optional.empty();
        }

        List<String> isoCodes = getProductionCountryIsoCodes(tmdbSeriesInfoRs.get());
        List<CountryRs> countryNames = countryGetStrategy.getCountriesByCodes(isoCodes);

        SeriesDetailViewRs seriesDetailViewRs = seriesDetailAdapter.toViewDto(tmdbSeriesInfoRs.get());
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
