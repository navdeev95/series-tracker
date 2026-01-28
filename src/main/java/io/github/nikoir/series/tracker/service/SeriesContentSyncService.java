package io.github.nikoir.series.tracker.service;
import io.github.nikoir.series.tracker.domain.entity.*;
import io.github.nikoir.series.tracker.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.series.tracker.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.dto.external.response.SeasonViewRs;
import io.github.nikoir.series.tracker.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.enums.Source;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeriesContentSyncService {
    private final SeasonEpisodeService seasonEpisodeService;
    private final EpisodeReleaseRepository episodeReleaseRepository;
    private final SourceRepository sourceRepository;

    public SyncResult syncSeriesContent(Series series,
                                        List<SeasonViewRs> externalSeasons,
                                        Source source) {
        log.info("Starting content sync for series: {}", series.getTitle());
        SeriesContent currentContent = new SeriesContent(seasonEpisodeService.loadCurrentContent(series.getId()));

        List<Episode> createdEpisodes = new ArrayList<>();
        SyncResult result = new SyncResult();

        for (SeasonViewRs externalSeason : externalSeasons) {
            createdEpisodes.addAll(syncSeasonContent(series, externalSeason, currentContent, result));
        }

        result.addNewReleases(createReleases(createdEpisodes, source));

        log.info("Sync completed: {} new seasons, {} new episodes, {} new releases",
                result.getNewSeasonsCnt(), result.getNewEpisodesCnt(), result.getNewReleasesCnt());
        return result;
    }

    private List<Episode> syncSeasonContent(Series series, SeasonViewRs externalSeason,
                                   SeriesContent currentContent, SyncResult result) {
        Season existingSeason = currentContent.getSeasonByNumber(externalSeason.number());

        if (existingSeason == null) {
            return createNewSeason(series, externalSeason, result).getEpisodes();
        } else {
            return syncEpisodes(series, existingSeason, externalSeason, result);
        }
    }

    private Season createNewSeason(Series series, SeasonViewRs externalSeason, SyncResult result) {
        log.info("New season detected for series '{}': season {}",
                series.getTitle(), externalSeason.number());

        Season season = seasonEpisodeService.createSeason(series, externalSeason);
        result.addNewSeason();
        result.addNewEpisodes(season.getEpisodes().size());

        return season;
    }

    private List<Episode> syncEpisodes(Series series, Season existingSeason,
                                       SeasonViewRs externalSeason, SyncResult result) {
        List<SeasonViewRs.EpisodeViewRs> newEpisodes = seasonEpisodeService
                .findMissingEpisodes(existingSeason, externalSeason);

        if (newEpisodes.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("New episodes detected for series '{}' season {}: {} episodes",
                series.getTitle(), externalSeason.number(), newEpisodes.size());

        List<Episode> createdEpisodes = seasonEpisodeService.createEpisodes(existingSeason, newEpisodes);
        result.addNewEpisodes(createdEpisodes.size());
        updateExistingEpisodes(existingSeason, externalSeason);

        return createdEpisodes;
    }

    private void updateExistingEpisodes(Season existingSeason, SeasonViewRs externalSeason) {
        // TODO: Добавить обновление существующих эпизодов
    }

    private List<EpisodeRelease> createReleases(List<Episode> createdEpisodes,
                                                Source source) {
        DictSource sourceEntity = sourceRepository.findByName(source.getName()).orElseThrow();

        List<EpisodeRelease> episodeReleases = createdEpisodes
                .stream()
                .map(episode ->
                    EpisodeRelease
                            .builder()
                            .episode(episode)
                            .source(sourceEntity)
                            .build())
                .toList();
        return episodeReleaseRepository.saveAll(episodeReleases);
    }

    // Вспомогательный класс для хранения текущего состояния
    @Value
    private static class SeriesContent {
        Map<Integer, Season> seasonsByNumber;

        SeriesContent(List<Season> seasons) {
            this.seasonsByNumber = seasons.stream()
                    .collect(Collectors.toMap(Season::getNumber, Function.identity()));
        }

        Season getSeasonByNumber(Integer number) {
            return seasonsByNumber.get(number);
        }
    }
}