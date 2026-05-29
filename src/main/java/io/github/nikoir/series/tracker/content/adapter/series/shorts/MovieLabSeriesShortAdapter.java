package io.github.nikoir.series.tracker.content.adapter.series.shorts;

import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.dto.integration.response.movielab.series.search.MovieLabSeriesSearchRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static io.github.nikoir.series.tracker.content.enums.ExternalId.KINOPOISK;

@Mapper(componentModel = "spring")
public abstract class MovieLabSeriesShortAdapter implements SeriesShortAdapter<MovieLabSeriesSearchRs> {
    protected boolean mapIsSeries(MovieLabSeriesSearchRs.SearchResult searchResult) {
        return searchResult != null && "serial".equals(searchResult.type());
    }

    protected Map<ExternalId, String> mapExternalIds(MovieLabSeriesSearchRs.SearchResult searchResult) {
        Map<ExternalId, String> result = new HashMap<>();

        Optional.ofNullable(searchResult)
                .map(MovieLabSeriesSearchRs.SearchResult::kinopoiskId)
                .ifPresent(id -> result.put(KINOPOISK, String.valueOf(id)));

        return result;
    }

    @Mapping(target = "title", source = "titleRu")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "posterUrl", source = "poster")
    @Mapping(target = "totalSeasons", ignore = true)
    @Mapping(target = "isSeries", source = ".")
    @Mapping(target = "externalIds", source = ".")
    abstract SeriesListViewRs toViewDto(MovieLabSeriesSearchRs.SearchResult searchResult);


    @Override
    public PagedModel<SeriesListViewRs> toViewDtoPage(MovieLabSeriesSearchRs searchResponse) {
        if (searchResponse == null || searchResponse.pagination() == null) {
            return new PagedModel<>(Page.empty());
        }

        if (CollectionUtils.isEmpty(searchResponse.results())) {
            return SeriesShortAdapter.createEmptyPage();
        }

        List<SeriesListViewRs> content = searchResponse.results()
                .stream()
                .map(this::toViewDto)
                .toList();

        var pagination = searchResponse.pagination();
        PageRequest pageRequest = PageRequest.of(
                pagination.page() > 0 ? pagination.page() - 1: 0,
                pagination.onPage()
        );

        PageImpl<SeriesListViewRs> seriesPage = new PageImpl<>(
                content,
                pageRequest,
                pagination.results()
        );

        return new PagedModel<>(seriesPage);
    }
}
