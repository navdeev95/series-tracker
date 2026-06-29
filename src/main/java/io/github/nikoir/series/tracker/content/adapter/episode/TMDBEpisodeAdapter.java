package io.github.nikoir.series.tracker.content.adapter.episode;

import io.github.nikoir.series.tracker.content.dto.integration.TMDBEpisodeInfoRs;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.series.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Date;

@Mapper(componentModel = "spring")
public abstract class TMDBEpisodeAdapter implements EpisodeAdapter<TMDBEpisodeInfoRs> {
    @Override
    @Mapping(source = "episodeNumber", target = "number")
    @Mapping(source = "airDate", target = "releaseDate", qualifiedByName = "stringToDate")
    public abstract EpisodeInfo toEpisodeInfo(TMDBEpisodeInfoRs source);

    @Named("stringToDate")
    public Date stringToDate(String dateString) {
        return DateUtils.stringToDate(dateString);
    }
}
