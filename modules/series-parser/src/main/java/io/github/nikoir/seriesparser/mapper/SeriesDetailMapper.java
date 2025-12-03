package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.dto.internal.SeriesDetailViewRs;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SeriesDetailMapper {
    public abstract Series toEntity(SeriesDetailViewRs dto);
}
