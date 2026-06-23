package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeasonRepository;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.mapper.EpisodeMapper;
import io.github.nikoir.series.tracker.content.mapper.SeasonMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeasonEpisodeService {

    private final SeasonRepository seasonRepository;
    private final SeriesRepository seriesRepository;
    private final EpisodeRepository episodeRepository;
    private final SeasonMapper seasonMapper;
    private final EpisodeMapper episodeMapper;

    public void createSeasonsWithEpisodes(Long seriesId, List<SeasonInfo> seasonList) {
        List<Season> seasonsToSave = new LinkedList<>();

        for (SeasonInfo seasonInfo: seasonList) {
            Season seasonToSave = seasonMapper.toEntity(seasonInfo);
            seasonToSave.setSeries(seriesRepository.getReferenceById(seriesId));

            List<Episode> episodesToSave = episodeMapper.toEntities(seasonInfo.getEpisodes());
            episodesToSave.forEach(episode -> episode.setSeason(seasonToSave));

            seasonToSave.setEpisodes(episodesToSave);

            seasonsToSave.add(seasonToSave);
        }

        seasonRepository.saveAll(seasonsToSave);
    }

    public List<Season> loadContentWithoutReleases(Long seriesId) {
        List<Season> seasons = seasonRepository.findSeasonsWithEpisodesWithoutReleases(seriesId);

        // Для каждого сезона загружаем только эпизоды без релизов
        seasons.forEach(season -> {
            List<Episode> episodesWithoutReleases =
                    episodeRepository.findEpisodesWithoutReleasesBySeasonId(season.getId());
            season.setEpisodes(episodesWithoutReleases);
        });

        return seasons;
    }
}