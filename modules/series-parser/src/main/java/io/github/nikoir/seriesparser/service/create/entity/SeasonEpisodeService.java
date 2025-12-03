package io.github.nikoir.seriesparser.service.create.entity;

import io.github.nikoir.seriesparser.domain.entity.Episode;
import io.github.nikoir.seriesparser.domain.entity.Season;
import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.domain.repo.EpisodeRepository;
import io.github.nikoir.seriesparser.domain.repo.SeasonRepository;
import io.github.nikoir.seriesparser.dto.internal.SeasonViewRs;
import io.github.nikoir.seriesparser.mapper.EpisodeMapper;
import io.github.nikoir.seriesparser.mapper.SeasonMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeasonEpisodeService {

    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final SeasonMapper seasonMapper;
    private final EpisodeMapper episodeMapper;

    public Season createSeason(Series series, SeasonViewRs seasonData) {
        log.info("Creating new season for series '{}': season {}",
                series.getTitle(), seasonData.number());

        Season newSeason = seasonMapper.toEntity(seasonData);
        newSeason.getEpisodes().forEach(episode -> episode.setSeason(newSeason));
        newSeason.setSeries(series);

        return seasonRepository.save(newSeason);
    }

    public List<Episode> createEpisodes(Season season, List<SeasonViewRs.EpisodeViewRs> episodesData) {
        log.info("Creating {} episodes for series '{}' season {}",
                episodesData.size(), season.getSeries().getTitle(), season.getNumber());

        List<Episode> episodes = episodeMapper.toEntities(episodesData);
        episodes.forEach(episode -> episode.setSeason(season));

        return episodeRepository.saveAll(episodes);
    }

    public List<SeasonViewRs.EpisodeViewRs> findMissingEpisodes(Season existingSeason, SeasonViewRs externalSeason) {
        Set<Integer> existingNumbers = existingSeason.getEpisodes()
                .stream()
                .map(Episode::getNumber)
                .collect(Collectors.toSet());

        return externalSeason.episodes()
                .stream()
                .filter(episode -> !existingNumbers.contains(episode.number()))
                .toList();
    }

    public List<Season> loadCurrentContent(Long seriesId) {
        return seasonRepository.findBySeriesIdWithEpisodes(seriesId);
    }
}