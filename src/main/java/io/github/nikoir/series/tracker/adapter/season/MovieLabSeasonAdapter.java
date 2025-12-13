package io.github.nikoir.series.tracker.adapter.season;

import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import io.github.nikoir.series.tracker.dto.api.response.movielab.episode.search.MovieLabEpisodeSearchRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class MovieLabSeasonAdapter implements ExternalSeasonAdapter<MovieLabEpisodeSearchRs.Season> {
    @Override
    @Mapping(source = "seasonNumber", target = "number")
    @Mapping(source = "episodes", target = "episodes", qualifiedByName = "mapEpisodes")
    @Mapping(target = "externalSource", constant = "MOVIE_LAB")
    public abstract SeasonViewRs toSeasonViewRs(MovieLabEpisodeSearchRs.Season source);

    @Mapping(source = "episode", target = "number")
    @Mapping(source = "translations", target = "translations", qualifiedByName = "mapTranslations")
    protected abstract SeasonViewRs.EpisodeViewRs mapEpisode(MovieLabEpisodeSearchRs.Episode episode);

    @Named("mapEpisodes")
    protected List<SeasonViewRs.EpisodeViewRs> mapEpisodes(Map<String, MovieLabEpisodeSearchRs.Episode> episodes) {
        if (CollectionUtils.isEmpty(episodes)) {
            return Collections.emptyList();
        }
        return episodes
                .values()
                .stream()
                .map(this::mapEpisode)
                .toList();
    }

    @Named("mapTranslations")
    protected List<String> mapTranslations(Map<String, MovieLabEpisodeSearchRs.Translation> translations) {
        if (CollectionUtils.isEmpty(translations)) {
            return Collections.emptyList();
        }

        return translations
                .values()
                .stream()
                .map(MovieLabEpisodeSearchRs.Translation::name)
                .toList();
    }
}
