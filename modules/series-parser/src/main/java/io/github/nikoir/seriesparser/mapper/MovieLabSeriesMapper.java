package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.dto.response.movielab.series.search.MovieLabSeriesSearchRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static io.github.nikoir.seriesparser.enums.ExternalId.KINOPOISK;

@Mapper(componentModel = "spring")
public abstract class MovieLabSeriesMapper implements ISeriesMapper<MovieLabSeriesSearchRs> {
    protected boolean mapIsSeries(MovieLabSeriesSearchRs.SearchResult searchResult) {
        return searchResult != null && "serial".equals(searchResult.type());
    }

    protected Map<String, String> mapExternalIds(MovieLabSeriesSearchRs.SearchResult searchResult) {
        Map<String, String> result = new HashMap<>();

        Optional.ofNullable(searchResult)
                .map(MovieLabSeriesSearchRs.SearchResult::kinopoiskId)
                .ifPresent(id -> result.put(KINOPOISK.getSourceName(), String.valueOf(id)));

        return result;
    }

    @Mapping(target = "title", source = "titleRu")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "posterUrl", source = "poster")
    @Mapping(target = "totalSeasons", ignore = true)
    @Mapping(target = "isSeries", source = ".")
    @Mapping(target = "externalIds", source = ".")
    abstract SeriesViewRs toViewDto(MovieLabSeriesSearchRs.SearchResult searchResult);


    @Override
    public PagedModel<SeriesViewRs> toViewDtoPage(MovieLabSeriesSearchRs searchResponse) {
        if (searchResponse == null || searchResponse.pagination() == null) {
            return new PagedModel<>(Page.empty());
        }

        List<SeriesViewRs> content = new ArrayList<>();
        if (!CollectionUtils.isEmpty(searchResponse.results())) {
            content = searchResponse.results()
                    .stream()
                    .map(this::toViewDto)
                    .toList();
        }

        var pagination = searchResponse.pagination();
        PageRequest pageRequest = PageRequest.of(
                pagination.page() > 0 ? pagination.page() - 1: 0,
                pagination.onPage()
        );

        PageImpl<SeriesViewRs> seriesPage = new PageImpl<>(
                content,
                pageRequest,
                pagination.results()
        );

        return new PagedModel<>(seriesPage);
    }
}
