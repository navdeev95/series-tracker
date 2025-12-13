package io.github.nikoir.series.tracker.adapter.series;

import io.github.nikoir.series.tracker.dto.api.response.kinopoisk.KinopoiskExternalId;
import io.github.nikoir.series.tracker.dto.api.response.kinopoisk.KinopoiskSeriesInfoRs;
import io.github.nikoir.series.tracker.dto.internal.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.dto.internal.SeriesStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;

import static io.github.nikoir.series.tracker.enums.ExternalId.KINOPOISK;

@Mapper(componentModel = "spring")
public abstract class KinopoiskSeriesDetailAdapter implements SeriesDetailAdapter<KinopoiskSeriesInfoRs> {
    @Override
    @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "title", source = "name")
    @Mapping(target = "engTitle", source = "enName")
    @Mapping(target = "totalSeasons", source = "seasonsInfo", qualifiedByName = "extractTotalSeasons")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "releaseYear", source = "year")
    @Mapping(target = "posterUrl", source = "poster.url")
    @Mapping(target = "externalIds", source = ".", qualifiedByName = "mapExternalIds")
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
            return SeriesStatus.ANNOUNCED;
        }

        return switch (status.toLowerCase()) {
            case "filming" -> SeriesStatus.FILMING;
            case "pre-production" -> SeriesStatus.PRE_PRODUCTION;
            case "completed" -> SeriesStatus.COMPLETED;
            case "post-production" -> SeriesStatus.POST_PRODUCTION;
            default -> SeriesStatus.ANNOUNCED;
        };
    }

    @Named("mapExternalIds")
    protected Map<String, String> mapExternalIds(KinopoiskSeriesInfoRs source) {
        if (source == null) {
            return new HashMap<>();
        }

        Map<String, String> result = new HashMap<>();

        // Основной KP ID
        Optional.of(source)
                .map(KinopoiskSeriesInfoRs::id)
                .map(Object::toString)
                .ifPresent(kpId -> result.put(KINOPOISK.getSourceName(), kpId));

        // External IDs
        Optional.of(source)
                .map(KinopoiskSeriesInfoRs::externalId)
                .ifPresent(externalIds ->
                        Arrays.stream(KinopoiskExternalId.values())
                                .filter(kpExtId -> kpExtId.getExternalId() != null)
                                .forEach(kpExtId -> {
                                    String externalIdValue = externalIds.get(kpExtId.getName());
                                    if (externalIdValue != null) {
                                        result.put(kpExtId.getExternalId().getSourceName(), externalIdValue);
                                    }
                                })
                );

        return result;
    }

}
