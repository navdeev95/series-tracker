package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.KinopoiskExternalId;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.KinopoiskSeriesInfoRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.*;

import static io.github.nikoir.seriesparser.enums.ExternalId.KINOPOISK;

@Mapper(componentModel = "spring")
public interface SeriesMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "title", source = "name")
    @Mapping(target = "engTitle", source = "enName")
    @Mapping(target = "totalSeasons", source = "seasonsInfo", qualifiedByName = "extractTotalSeasons")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "releaseYear", source = "year")
    @Mapping(target = "posterUrl", source = "poster.url")
    @Mapping(target = "externalIds", source = ".", qualifiedByName = "mapExternalIds")
    Series toEntity(KinopoiskSeriesInfoRs movieResponse);

    @Named("extractTotalSeasons")
    default Integer extractTotalSeasons(List<KinopoiskSeriesInfoRs.SeasonInfo> seasonsInfo) {
        if (seasonsInfo == null || seasonsInfo.isEmpty()) {
            return 0;
        }
        // Считаем количество сезонов (исключая спец-сезоны с number = 0)
        return (int) seasonsInfo.stream()
                .filter(season -> season.number() != null && season.number() > 0)
                .count();
    }

    @Named("mapStatus")
    default Series.Status mapStatus(String status) {
        if (status == null) {
            return Series.Status.ANNOUNCED;
        }

        return switch (status.toLowerCase()) {
            case "filming" -> Series.Status.FILMING;
            case "pre-production" -> Series.Status.PRE_PRODUCTION;
            case "completed" -> Series.Status.COMPLETED;
            case "post-production" -> Series.Status.POST_PRODUCTION;
            default -> Series.Status.ANNOUNCED;
        };
    }

    @Named("mapExternalIds")
    default Map<String, String> mapExternalIds(KinopoiskSeriesInfoRs source) {
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

    // Дополнительные методы для удобства
    default List<Series> toEntityList(List<KinopoiskSeriesInfoRs> movieResponses) {
        if (movieResponses == null) {
            return Collections.emptyList();
        }
        return movieResponses.stream()
                .map(this::toEntity)
                .toList();
    }

    // Метод для обновления существующей сущности
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "title", source = "name")
    @Mapping(target = "engTitle", source = "enName")
    @Mapping(target = "totalSeasons", source = "seasonsInfo", qualifiedByName = "extractTotalSeasons")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "releaseYear", source = "year")
    @Mapping(target = "posterUrl", source = "poster.url")
    @Mapping(target = "externalIds", source = "externalId")
    void updateEntity(@MappingTarget Series series, KinopoiskSeriesInfoRs movieResponse);
}
