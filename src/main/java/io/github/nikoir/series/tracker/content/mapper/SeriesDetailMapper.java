package io.github.nikoir.series.tracker.content.mapper;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;
import io.github.nikoir.series.tracker.content.domain.entity.Country;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.dto.internal.SeriesStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class SeriesDetailMapper {

    @Mapping(target = "externalIds", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "id", ignore = true)
    public abstract Series toEntity(SeriesDetailViewRs dto);

    @Named("mapStatus")
    public Series.Status mapStatus(SeriesStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case FILMING -> Series.Status.FILMING;
            case PRE_PRODUCTION -> Series.Status.PRE_PRODUCTION;
            case CONTINUING -> Series.Status.CONTINUING;
            case COMPLETED -> Series.Status.COMPLETED;
            case ANNOUNCED -> Series.Status.ANNOUNCED;
            case POST_PRODUCTION -> Series.Status.POST_PRODUCTION;
            case DELETED -> Series.Status.DELETED;
        };
    }


}
