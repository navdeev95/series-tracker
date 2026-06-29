package io.github.nikoir.series.tracker.content.service;
import io.github.nikoir.series.tracker.common.dto.response.EpisodeReleaseViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
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
public class SeriesReleaseSyncService {
    private final SeasonEpisodeService seasonEpisodeService;
    private final EpisodeReleaseRepository episodeReleaseRepository;
    private final SourceRepository sourceRepository;


    public List<EpisodeReleaseViewRs> syncReleases(Series series,
                                          List<SeasonInfo> externalSeasons,
                                          Source source) {
        log.info("Starting content sync for series with id: {}", series.getId());

        List<EpisodeReleaseViewRs> result = new ArrayList<>();

        List<Season> contentWithoutReleases = seasonEpisodeService.loadContentWithoutReleases(series.getId());
        List<Episode> missingEpisodes = new LinkedList<>();

        DictSource dictSource = sourceRepository.findByName(source.getName()).orElseThrow();
        Optional<String> seriesUrl = Source.buildUrl(dictSource, ExternalId.mapExternalIds(series.getExternalIds()));

        for (SeasonInfo externalSeason : externalSeasons) {
            Optional<Season> existingSeason = findExistingSeason(contentWithoutReleases, externalSeason.getNumber());

            if (existingSeason.isPresent())  {
                Season curSeason = existingSeason.get();
                List<Episode> curSeasonMissingEpisodes = getMissingEpisodes(curSeason, externalSeason);
                missingEpisodes.addAll(curSeasonMissingEpisodes);

                result.addAll(getEpisodeReleaseViews(curSeason, curSeasonMissingEpisodes, seriesUrl.orElse("")));
            }
        }

        if (!missingEpisodes.isEmpty()) {
            createReleases(missingEpisodes, dictSource);
        }

        log.info("Sync completed: {} new episodes", result.size());
        return result;
    }

    private List<EpisodeReleaseViewRs> getEpisodeReleaseViews(Season curSeason,
                                                              List<Episode> missingEpisodes,
                                                              String seriesUrl) {
        return missingEpisodes
                .stream()
                .map(e -> new EpisodeReleaseViewRs(curSeason.getNumber(), e.getNumber(), seriesUrl))
                .toList();
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
                                                DictSource sourceEntity) {
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