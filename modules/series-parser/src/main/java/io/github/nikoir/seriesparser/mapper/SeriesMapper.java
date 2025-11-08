package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.Doc;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.Image;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.NameSearchRs;
import io.github.nikoir.seriesparser.dto.response.movielab.search.MovieLabSearchRs;
import io.github.nikoir.seriesparser.dto.response.movielab.search.SearchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SeriesMapper {

    @Mapping(target="year", source="releaseYear")
    @Mapping(target = "isSeries", expression = "java(true)")
    SeriesViewRs toViewDto(Series series);

    default PagedModel<SeriesViewRs> toViewDtoPage(Page<Series> seriesPage) {
        return seriesPage == null ?
                new PagedModel<>(Page.empty()):
                new PagedModel<>(seriesPage.map(this::toViewDto));
    }

    default PagedModel<SeriesViewRs> toViewDtoPage(NameSearchRs searchRs) {
        if (searchRs == null) {
            return new PagedModel<>(Page.empty());
        }

        List<SeriesViewRs> content = new ArrayList<>();
        if (!CollectionUtils.isEmpty(searchRs.docs())) {
            content = searchRs
                    .docs()
                    .stream()
                    .map(this::toViewDto)
                    .toList();
        }
        PageRequest pageRequest = PageRequest.of(searchRs.page(), searchRs.limit());

        PageImpl<SeriesViewRs> seriesPage = new PageImpl<>(content, pageRequest, searchRs.total());

        return new PagedModel<>(seriesPage);

    }

    @Mapping(target="title", source = "name")
    @Mapping(target="posterUrl", source = "poster")
    SeriesViewRs toViewDto(Doc doc);

    default String mapPosterToPosterUrl(Image poster) {
        return Optional.ofNullable(poster)
                .map(Image::previewUrl)
                .orElse(null);
    }


    default PagedModel<SeriesViewRs> toViewDtoPage(MovieLabSearchRs searchResponse) {
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
                pagination.page() - 1, // MovieLab использует 1-based, Spring - 0-based
                pagination.onPage()
        );

        PageImpl<SeriesViewRs> seriesPage = new PageImpl<>(
                content,
                pageRequest,
                pagination.results()
        );

        return new PagedModel<>(seriesPage);
    }

    @Mapping(target = "title", source = "titleRu")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "posterUrl", source = "poster")
    @Mapping(target = "totalSeasons", constant = "0") // MovieLab не предоставляет информацию о сезонах
    @Mapping(target = "isSeries", expression = "java(isSeries(searchResult))")
    SeriesViewRs toViewDto(SearchResult searchResult);

    default boolean isSeries(SearchResult searchResult) {
        return searchResult != null && "serial".equals(searchResult.type());
    }
}
