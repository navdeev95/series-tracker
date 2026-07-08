package io.github.nikoir.tracker.content.mapper;

import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.common.dto.response.SeriesStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class SeriesDetailMapper {

    @Mapping(target = "externalIds", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "id", ignore = true)
    public abstract Series toEntity(SeriesDetailViewRs dto);

    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "countries", ignore = true)
    @Mapping(target = "externalIds", ignore = true)
    public abstract void updateEntity(SeriesDetailViewRs dto, @MappingTarget Series entity);

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
