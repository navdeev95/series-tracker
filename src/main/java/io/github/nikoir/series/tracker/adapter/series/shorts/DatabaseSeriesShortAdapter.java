package io.github.nikoir.series.tracker.adapter.series.shorts;

import io.github.nikoir.series.tracker.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class DatabaseSeriesShortAdapter implements SeriesShortAdapter<Page<Series>> {
    @Mapping(target="year", source="releaseYear")
    @Mapping(target = "isSeries", expression = "java(true)")
    @Mapping(target = "externalIds", source = "externalIds")
    abstract SeriesShortViewRs toViewDto(Series series);

    @Override
    public PagedModel<SeriesShortViewRs> toViewDtoPage(Page<Series> seriesPage) {
        return seriesPage == null ?
                new PagedModel<>(Page.empty()):
                new PagedModel<>(seriesPage.map(this::toViewDto));
    }

    protected Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIds) {
        Map<ExternalId, String> result = new HashMap<>();
        for (ExternalIdSeries externalId: externalIds) {
            ExternalId foundExternalId = ExternalId.fromId(externalId.getId());
            result.put(foundExternalId, externalId.getValue());
        }
        return result;
    }
}
