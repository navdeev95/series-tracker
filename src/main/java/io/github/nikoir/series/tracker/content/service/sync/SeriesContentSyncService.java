package io.github.nikoir.series.tracker.content.service.sync;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.service.SeasonEpisodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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


    public SyncResult syncSeriesContent(Long seriesId,
                                        List<SeasonViewRs> externalSeasons,
                                        Source source) {
        log.info("Starting content sync for series with id: {}", seriesId);
        SeriesContent currentContent = new SeriesContent(seasonEpisodeService.loadCurrentContent(seriesId));

        List<Episode> createdEpisodes = new ArrayList<>();
        SyncResult result = new SyncResult();

        for (SeasonViewRs externalSeason : externalSeasons) {
            createdEpisodes.addAll(syncSeasonContent(seriesId, externalSeason, currentContent, result));
        }
        if (!createdEpisodes.isEmpty()) {
            createReleases(createdEpisodes, source);
        }

        log.info("Sync completed: {} new seasons, {} new episodes, {} new releases",
                result.getNewSeasonsCnt(), result.getNewEpisodesCnt(), result.getNewReleasesCnt());
        return result;
    }

    private List<Episode> syncSeasonContent(Long seriesId, SeasonViewRs externalSeason,
                                   SeriesContent currentContent, SyncResult result) {
        Optional<Season> existingSeason = currentContent.getSeasonByNumber(externalSeason.number());
        return existingSeason.map(season -> syncEpisodes(seriesId, season, externalSeason, result))
                .orElseGet(() -> createNewSeason(seriesId, externalSeason, result).getEpisodes());
    }

    private Season createNewSeason(Long seriesId, SeasonViewRs externalSeason, SyncResult result) {
        log.info("New season detected for series with id'{}': season {}",
                seriesId, externalSeason.number());

        Season season = seasonEpisodeService.createSeason(seriesId, externalSeason);
        result.addNewSeason();
        result.addNewEpisodes(season.getEpisodes().size());

        return season;
    }

    private List<Episode> syncEpisodes(Long seriesId, Season existingSeason,
                                       SeasonViewRs externalSeason, SyncResult result) {
        List<SeasonViewRs.EpisodeViewRs> newEpisodes = seasonEpisodeService
                .findMissingEpisodes(existingSeason, externalSeason);

        if (newEpisodes.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("New episodes detected for series with id '{}' season {}: {} episodes",
                seriesId, externalSeason.number(), newEpisodes.size());

        List<Episode> createdEpisodes = seasonEpisodeService.createEpisodes(existingSeason, newEpisodes);
        result.addNewEpisodes(createdEpisodes.size());
        updateExistingEpisodes(existingSeason, externalSeason);

        return createdEpisodes;
    }

    private void updateExistingEpisodes(Season existingSeason, SeasonViewRs externalSeason) {
        // TODO: Добавить обновление существующих эпизодов
    }

    private void createReleases(List<Episode> createdEpisodes,
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
        episodeReleaseRepository.saveAll(episodeReleases);
    }

    // Вспомогательный класс для хранения текущего состояния
    @Value
    public static class SeriesContent {
        Map<Integer, Season> seasonsByNumber;

        public SeriesContent(List<Season> seasons) {
            this.seasonsByNumber = seasons.stream()
                    .collect(Collectors.toMap(Season::getNumber, Function.identity()));
        }

        Optional<Season> getSeasonByNumber(Integer number) {
            return Optional.ofNullable(seasonsByNumber.get(number));
        }
    }
}