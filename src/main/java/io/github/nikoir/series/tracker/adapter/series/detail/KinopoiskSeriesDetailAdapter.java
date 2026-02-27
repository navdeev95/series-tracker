package io.github.nikoir.series.tracker.adapter.series.detail;

import io.github.nikoir.series.tracker.adapter.series.KinopoiskExternalIdAdapter;
import io.github.nikoir.series.tracker.dto.integration.response.kinopoisk.KinopoiskSeriesInfoRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.dto.internal.SeriesStatus;
import io.github.nikoir.series.tracker.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Mapper(componentModel = "spring")
public abstract class KinopoiskSeriesDetailAdapter implements SeriesDetailAdapter<KinopoiskSeriesInfoRs> {
    @Autowired
    private KinopoiskExternalIdAdapter externalIdAdapter;

    @Override
    @Mapping(target = "title", source = "name")
    @Mapping(target = "engTitle", source = "enName")
    @Mapping(target = "totalSeasons", source = "seasonsInfo", qualifiedByName = "extractTotalSeasons")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "releaseYear", source = "year")
    @Mapping(target = "posterUrl", source = "poster.url")
    @Mapping(target = "externalIds", source = ".")
    @Mapping(target = "countries", source = ".")
    public abstract SeriesDetailViewRs toViewDto(KinopoiskSeriesInfoRs source);

    @Named("extractTotalSeasons")
    protected Integer extractTotalSeasons(List<KinopoiskSeriesInfoRs.SeasonInfo> seasonsInfo) {
        if (seasonsInfo == null || seasonsInfo.isEmpty()) {
            return 0;
        }
        // Считаем количество сезонов (исключая спец-сезоны с number = 0)
        return (int) seasonsInfo.stream()
                .filter(season -> season.number() != null && season.number() > 0)
                .count();
    }

    @Named("mapStatus")
    protected SeriesStatus mapStatus(String status) {
        if (status == null) {
            return null;
        }

        return switch (status.toLowerCase()) {
            case "filming" -> SeriesStatus.FILMING;
            case "pre-production" -> SeriesStatus.PRE_PRODUCTION;
            case "completed" -> SeriesStatus.COMPLETED;
            case "post-production" -> SeriesStatus.POST_PRODUCTION;
            case "deleted" -> SeriesStatus.DELETED;
            case "announced" -> SeriesStatus.ANNOUNCED;
            default -> null;
        };
    }

    protected Map<ExternalId, String> mapExternalIds(KinopoiskSeriesInfoRs source) {
        return externalIdAdapter.mapExternalIds(String.valueOf(source.id()), source.externalId());
    }

    protected List<String> mapCountries(KinopoiskSeriesInfoRs source) {
        if (CollectionUtils.isEmpty(source.countries())) {
            return Collections.emptyList();
        }

        return source.countries()
                .stream()
                .map(KinopoiskSeriesInfoRs.Country::name)
                .toList();
    }

}
