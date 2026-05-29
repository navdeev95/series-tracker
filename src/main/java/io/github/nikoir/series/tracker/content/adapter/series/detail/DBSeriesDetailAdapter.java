package io.github.nikoir.series.tracker.content.adapter.series.detail;

import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.dto.internal.SeriesStatus;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class DBSeriesDetailAdapter implements SeriesDetailAdapter<Series>{
    @Override
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "externalIds", source = "externalIds")
    @Mapping(target = "isSeries", constant = "true")
    public abstract SeriesDetailViewRs toViewDto(Series source);

    @Named("mapStatus")
    protected SeriesStatus mapStatus(Series.Status status) {
        if (status == null) {
            return null;
        }

        return switch (status) {
            case FILMING -> SeriesStatus.FILMING;
            case PRE_PRODUCTION -> SeriesStatus.PRE_PRODUCTION;
            case COMPLETED -> SeriesStatus.COMPLETED;
            case POST_PRODUCTION -> SeriesStatus.POST_PRODUCTION;
            case DELETED -> SeriesStatus.DELETED;
            case ANNOUNCED -> SeriesStatus.ANNOUNCED;
        };
    }

    protected Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIds) {
        return ExternalId.mapExternalIds(externalIds);
    }
}
