package io.github.nikoir.series.tracker.content.adapter.series.detail;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Country;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.series.tracker.content.dto.internal.SeriesStatus;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class TMDBSeriesDetailAdapter implements SeriesDetailAdapter<TMDBSeriesInfoRs> {

    @Override
    @Mapping(target = "innerId", ignore = true)
    @Mapping(target = "title", source = "name")
    @Mapping(target = "engTitle", source = "originalName")
    @Mapping(target = "totalSeasons", source = "seasons", qualifiedByName = "extractTotalSeasons")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "releaseYear", source = "firstAirDate", qualifiedByName = "extractYearFromDate")
    @Mapping(target = "posterUrl", source = "posterPath", qualifiedByName = "buildPosterUrl")
    @Mapping(target = "description", source = "overview")
    @Mapping(target = "countries", source = "productionCountries", qualifiedByName = "mapCountries")
    @Mapping(target = "isSeries", constant = "true")
    @Mapping(target = "externalIds", source = ".")
    public abstract SeriesDetailViewRs toViewDto(TMDBSeriesInfoRs source);

    @Named("extractTotalSeasons")
    protected Integer extractTotalSeasons(List<TMDBSeriesInfoRs.Season> seasons) {
        if (seasons == null || seasons.isEmpty()) {
            return 0;
        }
        // Считаем количество сезонов (исключая спец-сезоны с season_number = 0)
        return (int) seasons.stream()
                .filter(season -> season.seasonNumber() != null && season.seasonNumber() > 0)
                .count();
    }

    @Named("mapStatus")
    protected SeriesStatus mapStatus(String status) {
        if (status == null) {
            return null;
        }

        return switch (status.toLowerCase()) {
            case "returning series" -> SeriesStatus.CONTINUING;
            case "in production" -> SeriesStatus.FILMING;
            case "planned" -> SeriesStatus.ANNOUNCED;
            case "ended" -> SeriesStatus.COMPLETED;
            case "canceled" -> SeriesStatus.DELETED;
            case "pilot" -> SeriesStatus.PRE_PRODUCTION;
            default -> null;
        };
    }

    @Named("extractYearFromDate")
    protected Integer extractYearFromDate(String firstAirDate) {
        if (firstAirDate == null || firstAirDate.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(firstAirDate.split("-")[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Named("buildPosterUrl")
    protected String buildPosterUrl(String posterPath) {
        if (posterPath == null || posterPath.trim().isEmpty()) {
            return null;
        }
        // Базовый URL для TMDB изображений (можно вынести в конфиг)
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    @Named("mapCountries")
    protected List<CountryRs> mapCountries(List<TMDBSeriesInfoRs.ProductionCountry> productionCountries) {
        if (CollectionUtils.isEmpty(productionCountries)) {
            return Collections.emptyList();
        }

        return productionCountries.stream()
                .map(pc -> new CountryRs(pc.iso3166_1(), pc.name()))
                .toList();
    }

    protected Map<ExternalId, String> mapExternalIds(TMDBSeriesInfoRs source) {
        Map<ExternalId, String> externalIds = new HashMap<>();

        // Добавляем TMDB ID
        if (source.id() != null) {
            externalIds.put(ExternalId.TMDB, String.valueOf(source.id()));
        }

        return externalIds;
    }
}