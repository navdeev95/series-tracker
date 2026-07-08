package io.github.nikoir.tracker.content.adapter.episode;

import io.github.nikoir.tracker.content.dto.integration.TMDBEpisodeInfoRs;
import io.github.nikoir.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.tracker.content.util.DateUtils;
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
