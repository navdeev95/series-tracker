package io.github.nikoir.series.tracker.factory;

import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.domain.entity.Season;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class SeasonTestFactory {
    public static SeasonInfo fromSeason(Season season) {
        return new SeasonInfo(season.getName(),
                season.getNumber(),
                Date.from(season.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)),
                Stream.ofNullable(season.getEpisodes())
                        .flatMap(Collection::stream)
                        .map(EpisodeTestFactory::fromEpisode).toList());
    }

    public static List<SeasonInfo> fromSeasons(List<Season> seasonList) {
        if (seasonList == null || seasonList.isEmpty()) {
            return Collections.emptyList();
        }

        return seasonList.stream()
                .map(SeasonTestFactory::fromSeason)
                .toList();
    }
}
