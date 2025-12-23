package io.github.nikoir.series.tracker.mapper;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.internal.SeriesDetailViewRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SeriesDetailMapper {
    @Mapping(target = "externalIds", ignore = true)
    public abstract Series toEntity(SeriesDetailViewRs dto);
}
