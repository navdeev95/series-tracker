package io.github.nikoir.series.tracker.builder.dto;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

public class SeasonViewRsBuilder {

    public SeasonViewRs fromSeason(Season season) {
        return new SeasonViewRs(season.getName(),
                season.getNumber(),
                Date.from(season.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)),
                Stream.ofNullable(season.getEpisodes())
                        .flatMap(Collection::stream)
                        .map(this::fromEpisode).toList());
    }

    public SeasonViewRs.EpisodeViewRs fromEpisode(Episode episode) {
        return new SeasonViewRs.EpisodeViewRs(episode.getName(),
                episode.getNumber(),
                Collections.emptyList(),
                Date.from(episode.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)));
    }
}
