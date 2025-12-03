package io.github.nikoir.seriesparser.adapter.series;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.dto.internal.SeriesShortViewRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

@Mapper(componentModel = "spring")
public abstract class DatabaseSeriesShortAdapter implements SeriesShortAdapter<Page<Series>> {
    @Mapping(target="year", source="releaseYear")
    @Mapping(target = "isSeries", expression = "java(true)")
    abstract SeriesShortViewRs toViewDto(Series series);

    @Override
    public PagedModel<SeriesShortViewRs> toViewDtoPage(Page<Series> seriesPage) {
        return seriesPage == null ?
                new PagedModel<>(Page.empty()):
                new PagedModel<>(seriesPage.map(this::toViewDto));
    }
}
