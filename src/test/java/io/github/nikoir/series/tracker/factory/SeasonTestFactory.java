package io.github.nikoir.series.tracker.factory;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Season;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

public class SeasonTestFactory {
    public static SeasonViewRs fromSeason(Season season) {
        return new SeasonViewRs(season.getName(),
                season.getNumber(),
                Date.from(season.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)),
                Stream.ofNullable(season.getEpisodes())
                        .flatMap(Collection::stream)
                        .map(EpisodeTestFactory::fromEpisode).toList());
    }
}
