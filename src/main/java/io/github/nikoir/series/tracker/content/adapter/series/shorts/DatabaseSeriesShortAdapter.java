package io.github.nikoir.series.tracker.content.adapter.series.shorts;

import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class DatabaseSeriesShortAdapter implements SeriesShortAdapter<Page<Series>> {
    @Mapping(target="year", source="releaseYear")
    @Mapping(target = "isSeries", expression = "java(true)")
    @Mapping(target = "externalIds", source = "externalIds")
    abstract SeriesListViewRs toViewDto(Series series);

    @Override
    public PagedModel<SeriesListViewRs> toViewDtoPage(Page<Series> seriesPage) {
        return seriesPage == null ?
                SeriesShortAdapter.createEmptyPage():
                new PagedModel<>(seriesPage.map(this::toViewDto));
    }

    protected Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIds) {
        return ExternalId.mapExternalIds(externalIds);
    }
}
