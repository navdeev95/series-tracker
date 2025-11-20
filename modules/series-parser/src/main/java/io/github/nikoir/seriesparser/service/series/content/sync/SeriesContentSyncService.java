package io.github.nikoir.seriesparser.service.series.content.sync;

import io.github.nikoir.seriesparser.domain.entity.Episode;
import io.github.nikoir.seriesparser.domain.entity.Season;
import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.domain.repo.EpisodeRepository;
import io.github.nikoir.seriesparser.domain.repo.SeasonRepository;
import io.github.nikoir.seriesparser.dto.SyncResult;
import io.github.nikoir.seriesparser.dto.response.SeasonViewRs;
import io.github.nikoir.seriesparser.mapper.EpisodeMapper;
import io.github.nikoir.seriesparser.mapper.SeasonMapper;
import io.github.nikoir.seriesparser.service.episode.search.ExternalContentSearchStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeriesContentSyncService {  // Более точное название
    private final ExternalContentSearchStrategy contentSearchStrategy;
    private final SeasonMapper seasonMapper;
    private final EpisodeMapper episodeMapper;  // Разделить ответственность

    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;

    public SyncResult syncSeriesContent(Series series) {
        log.info("Starting content sync for series: {}", series.getTitle());

        List<SeasonViewRs> externalSeasons = contentSearchStrategy.search(series.getExternalIds());
        SeriesContent currentContent = loadCurrentContent(series.getId());

        SyncResult result = new SyncResult();

        for (SeasonViewRs externalSeason : externalSeasons) {
            syncSeasonContent(series, externalSeason, currentContent, result);
        }

        log.info("Sync completed: {} new seasons, {} new episodes",
                result.getNewSeasons(), result.getNewEpisodes());
        return result;
    }

    private SeriesContent loadCurrentContent(Long seriesId) {
        List<Season> seasons = seasonRepository.findBySeriesIdWithEpisodes(seriesId);
        return new SeriesContent(seasons);
    }

    private void syncSeasonContent(Series series, SeasonViewRs externalSeason,
                                   SeriesContent currentContent, SyncResult result) {
        Season existingSeason = currentContent.getSeasonByNumber(externalSeason.number());

        if (existingSeason == null) {
            createNewSeason(series, externalSeason, result);
        } else {
            syncEpisodes(series, existingSeason, externalSeason, result);
        }
    }

    private void createNewSeason(Series series, SeasonViewRs externalSeason, SyncResult result) {
        log.info("New season detected for series '{}': season {}",
                series.getTitle(), externalSeason.number());

        Season newSeason = seasonMapper.toEntity(externalSeason);
        newSeason.getEpisodes().forEach(episode -> episode.setSeason(newSeason));
        newSeason.setSeries(series);

        seasonRepository.save(newSeason);

        result.incrementNewSeasons();
        result.addNewEpisodes(newSeason.getEpisodes().size());
    }

    private void syncEpisodes(Series series, Season existingSeason,
                              SeasonViewRs externalSeason, SyncResult result) {
        Set<Integer> existingEpisodeNumbers = existingSeason.getEpisodes()
                .stream()
                .map(Episode::getNumber)
                .collect(Collectors.toSet());

        List<SeasonViewRs.EpisodeViewRs> newEpisodes = externalSeason.episodes()
                .stream()
                .filter(episode -> !existingEpisodeNumbers.contains(episode.number()))
                .toList();

        if (!newEpisodes.isEmpty()) {
            log.info("New episodes detected for series '{}' season {}: {} episodes",
                    series.getTitle(), externalSeason.number(), newEpisodes.size());

            List<Episode> episodesToSave = episodeMapper.toEntities(newEpisodes);
            episodesToSave.forEach(episode -> episode.setSeason(existingSeason));

            episodeRepository.saveAll(episodesToSave);
            result.addNewEpisodes(episodesToSave.size());
        }

        // TODO: Добавить обновление существующих эпизодов
        updateExistingEpisodes(existingSeason, externalSeason);
    }

    private void updateExistingEpisodes(Season existingSeason, SeasonViewRs externalSeason) {
        // Логика обновления названий, дат выхода и т.д.
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