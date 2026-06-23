package io.github.nikoir.series.tracker.content.service;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.Source;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeriesContentSyncService {
    private final SeasonEpisodeService seasonEpisodeService;
    private final EpisodeReleaseRepository episodeReleaseRepository;
    private final SourceRepository sourceRepository;


    public SyncResult syncSeriesContent(Long seriesId,
                                        List<SeasonInfo> externalSeasons,
                                        Source source) {
        log.info("Starting content sync for series with id: {}", seriesId);

        SyncResult result = new SyncResult();
        List<Season> contentWithoutReleases = seasonEpisodeService.loadContentWithoutReleases(seriesId);
        List<Episode> missingEpisodes = new LinkedList<>();
        List<SeasonInfo> newSeasons = new LinkedList<>();

        for (SeasonInfo externalSeason : externalSeasons) {
            Optional<Season> existingSeason = findExistingSeason(contentWithoutReleases, externalSeason.getNumber());

            if (existingSeason.isEmpty())  {
                result.addNewSeason();
                newSeasons.add(externalSeason);
            } else {
                Season curSeason = existingSeason.get();
                List<Episode> curSeasonMissingEpisodes = getMissingEpisodes(curSeason, externalSeason);

                result.addNewEpisodes(curSeasonMissingEpisodes.size());
                missingEpisodes.addAll(curSeasonMissingEpisodes);
            }
        }

        if (!newSeasons.isEmpty()) {
            seasonEpisodeService.createSeasonsWithEpisodes(seriesId, newSeasons);
        }

        if (!missingEpisodes.isEmpty()) {
            createReleases(missingEpisodes, source);
        }

        log.info("Sync completed: {} new seasons, {} new episodes",
                result.getNewSeasonsCnt(), result.getNewEpisodesCnt());
        return result;
    }

    private Optional<Season> findExistingSeason(List<Season> existingSeasons, Integer seasonNumber) {
        return existingSeasons
                .stream()
                .filter(s -> Objects.equals(s.getNumber(), seasonNumber))
                .findFirst();
    }

    private List<Episode> getMissingEpisodes(Season currentSeason, SeasonInfo externalSeason) {
        return currentSeason
                .getEpisodes()
                .stream()
                .filter(e -> isExternalSeasonContainsEpisode(externalSeason, e.getNumber()))
                .toList();
    }

    private boolean isExternalSeasonContainsEpisode(SeasonInfo externalSeason, Integer episodeNumber) {
        return externalSeason
                .getEpisodes()
                .stream()
                .anyMatch(e -> Objects.equals(e.getNumber(), episodeNumber));
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
}