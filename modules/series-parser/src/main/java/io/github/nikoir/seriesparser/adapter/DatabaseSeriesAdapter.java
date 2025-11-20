package io.github.nikoir.seriesparser.adapter;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

@Mapper(componentModel = "spring")
public abstract class DatabaseSeriesAdapter implements SeriesAdapter<Page<Series>> {
    @Mapping(target="year", source="releaseYear")
    @Mapping(target = "isSeries", expression = "java(true)")
    abstract SeriesViewRs toViewDto(Series series);

    @Override
    public PagedModel<SeriesViewRs> toViewDtoPage(Page<Series> seriesPage) {
        return seriesPage == null ?
                new PagedModel<>(Page.empty()):
                new PagedModel<>(seriesPage.map(this::toViewDto));
    }
}
