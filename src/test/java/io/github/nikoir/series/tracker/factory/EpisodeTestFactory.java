package io.github.nikoir.series.tracker.factory;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EpisodeTestFactory {
    public static SeasonViewRs.EpisodeViewRs fromEpisode(Episode episode) {
        return new SeasonViewRs.EpisodeViewRs(episode.getName(),
                episode.getNumber(),
                Collections.emptyList(),
                Date.from(episode.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)));
    }

    public static List<Episode> fromEpisodeViewList(List<SeasonViewRs.EpisodeViewRs> episodeViewRsList) {
        return episodeViewRsList
                .stream()
                .map(EpisodeTestFactory::fromEpisodeView)
                .toList();
    }

    public static Episode fromEpisodeView(SeasonViewRs.EpisodeViewRs episodeViewRs) {
        return Episode.builder()
                .name(episodeViewRs.name())
                .number(episodeViewRs.number())
                .releaseDate(LocalDate.ofInstant(episodeViewRs.releaseDate().toInstant(), ZoneOffset.UTC))
                .build();
    }
}
