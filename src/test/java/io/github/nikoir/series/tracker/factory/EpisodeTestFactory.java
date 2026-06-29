package io.github.nikoir.series.tracker.factory;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.dto.internal.EpisodeInfo;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class EpisodeTestFactory {
    public static EpisodeInfo fromEpisode(Episode episode) {
        return new EpisodeInfo(episode.getName(),
                episode.getNumber(),
                Date.from(episode.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)));
    }

    public static List<Episode> fromEpisodeViewList(List<EpisodeInfo> episodeInfoList) {
        return episodeInfoList
                .stream()
                .map(EpisodeTestFactory::fromEpisodeView)
                .toList();
    }

    public static Episode fromEpisodeView(EpisodeInfo episodeInfo) {
        return Episode.builder()
                .name(episodeInfo.getName())
                .number(episodeInfo.getNumber())
                .releaseDate(LocalDate.ofInstant(episodeInfo.getReleaseDate().toInstant(), ZoneOffset.UTC))
                .build();
    }
}
