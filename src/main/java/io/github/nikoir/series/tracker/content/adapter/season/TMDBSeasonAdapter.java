package io.github.nikoir.series.tracker.content.adapter.season;

import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Date;

@Mapper(componentModel = "spring")
public abstract class TMDBSeasonAdapter implements ExternalSeasonAdapter<TMDBSeriesInfoRs.Season> {

    @Override
    @Mapping(target = "name", source = "name")
    @Mapping(target = "number", source = "seasonNumber")
    @Mapping(target = "releaseDate", source = "airDate", qualifiedByName = "stringToDate")
    public abstract SeasonInfo toSeasonInfo(TMDBSeriesInfoRs.Season source);

    @Named("stringToDate")
    protected Date stringToDate(String dateString) {
        return DateUtils.stringToDate(dateString);
    }
}
