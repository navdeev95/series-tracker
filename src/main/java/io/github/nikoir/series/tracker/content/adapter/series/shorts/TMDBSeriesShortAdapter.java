package io.github.nikoir.series.tracker.content.adapter.series.shorts;

import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.dto.integration.response.tmdb.TMDBSeriesSearchRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Mapper(componentModel = "spring")
public abstract class TMDBSeriesShortAdapter implements SeriesShortAdapter<TMDBSeriesSearchRs> {

    @Mapping(target = "title", source = ".", qualifiedByName = "getTitleWithPriority")
    @Mapping(target = "year", source = "firstAirDate", qualifiedByName = "extractYearFromDate")
    @Mapping(target = "posterUrl", source = "posterPath", qualifiedByName = "buildPosterUrl")
    @Mapping(target = "totalSeasons", ignore = true)
    @Mapping(target = "externalIds", source = ".", qualifiedByName = "mapExternalIds")
    @Mapping(target = "isSeries", constant = "true")
    abstract SeriesListViewRs toViewDto(TMDBSeriesSearchRs.Result result);

    @Named("getTitleWithPriority")
    protected String getTitleWithPriority(TMDBSeriesSearchRs.Result result) {
        if (result.name() != null && !result.name().trim().isEmpty()) {
            return result.name();
        }
        if (result.originalName() != null && !result.originalName().trim().isEmpty()) {
            return result.originalName();
        }
        return "";
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
        // Базовый URL для TMDB изображений
        return "https://image.tmdb.org/t/p/w92" + posterPath;
    }

    @Named("mapExternalIds")
    protected Map<ExternalId, String> mapExternalIds(TMDBSeriesSearchRs.Result result) {
        Map<ExternalId, String> externalIds = new HashMap<>();

        externalIds.put(ExternalId.TMDB, String.valueOf(result.id()));

        return externalIds;
    }

    @Override
    public PagedModel<SeriesListViewRs> toViewDtoPage(TMDBSeriesSearchRs searchRs) {
        if (searchRs == null) {
            return new PagedModel<>(Page.empty());
        }

        if (CollectionUtils.isEmpty(searchRs.results())) {
            return SeriesShortAdapter.createEmptyPage();
        }

        List<SeriesListViewRs> content = searchRs
                .results()
                .stream()
                .map(this::toViewDto)
                .toList();

        PageRequest pageRequest = PageRequest.of(searchRs.page() - 1, searchRs.totalResults());

        PageImpl<SeriesListViewRs> seriesPage = new PageImpl<>(content, pageRequest, searchRs.totalResults());

        return new PagedModel<>(seriesPage);
    }
}