package io.github.nikoir.tracker.content.domain;

import io.github.nikoir.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.tracker.builder.domain.EpisodeReleaseBuilder;
import io.github.nikoir.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.tracker.content.domain.entity.Episode;
import io.github.nikoir.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.tracker.content.domain.entity.Season;
import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.tracker.content.domain.repo.EpisodeRepository;
import io.github.nikoir.tracker.content.domain.repo.SeasonRepository;
import io.github.nikoir.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.tracker.content.domain.repo.SourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.nikoir.tracker.content.enums.Source.MOVIELAB;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EpisodeRepositoryTest {
    private static final int EPISODES_COUNT = 10;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private SourceRepository sourceRepository;

    private Season seasonWithAllReleases;
    private Season seasonWithHalfReleases;
    private Season seasonWithoutReleases;
    private DictSource source;

    @BeforeEach
    void setUp() {
        this.source = sourceRepository
                .findByName(MOVIELAB.getName())
                .orElseThrow();

        // Создаем серию с тремя сезонами
        Series series = new SeriesBuilder().build();
        Series savedSeries = seriesRepository.save(series);

        // Сезон 1: все эпизоды с релизами
        seasonWithAllReleases = createAndSaveSeason(savedSeries, 1, EPISODES_COUNT, true);

        // Сезон 2: половина эпизодов с релизами (четные)
        seasonWithHalfReleases = createAndSaveSeason(savedSeries, 2, EPISODES_COUNT, false);

        // Сезон 3: все эпизоды без релизов
        seasonWithoutReleases = createAndSaveSeason(savedSeries, 3, EPISODES_COUNT, null);
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_WhenAllEpisodesHaveReleases_ShouldReturnEmptyList() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(seasonWithAllReleases.getId());

        // Then
        assertNotNull(episodes);
        assertTrue(episodes.isEmpty(), "Should return empty list when all episodes have releases");
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_WhenHalfEpisodesHaveReleases_ShouldReturnOnlyEpisodesWithoutReleases() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(seasonWithHalfReleases.getId());

        // Then
        assertNotNull(episodes);
        assertEquals(EPISODES_COUNT / 2, episodes.size(),
                "Should return exactly half of the episodes (those without releases)");

        // Проверяем, что вернулись только нечетные эпизоды (без релизов)
        List<Integer> episodeNumbers = episodes.stream()
                .map(Episode::getNumber)
                .sorted()
                .toList();

        List<Integer> expectedNumbers = IntStream.rangeClosed(1, EPISODES_COUNT)
                .filter(n -> n % 2 != 0) // нечетные номера
                .boxed()
                .toList();

        assertEquals(expectedNumbers, episodeNumbers,
                "Should return only odd-numbered episodes (without releases)");

        // Проверяем, что у всех возвращенных эпизодов действительно нет релизов
        boolean allWithoutReleases = episodes.stream()
                .allMatch(e -> e.getReleases() == null || e.getReleases().isEmpty());
        assertTrue(allWithoutReleases, "All returned episodes should be without releases");
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_WhenNoEpisodesHaveReleases_ShouldReturnAllEpisodes() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(seasonWithoutReleases.getId());

        // Then
        assertNotNull(episodes);
        assertEquals(EPISODES_COUNT, episodes.size(),
                "Should return all episodes when none have releases");

        // Проверяем, что вернулись все номера эпизодов
        List<Integer> episodeNumbers = episodes.stream()
                .map(Episode::getNumber)
                .sorted()
                .toList();

        List<Integer> expectedNumbers = IntStream.rangeClosed(1, EPISODES_COUNT)
                .boxed()
                .toList();

        assertEquals(expectedNumbers, episodeNumbers,
                "Should return all episode numbers from 1 to " + EPISODES_COUNT);

        // Проверяем, что у всех эпизодов действительно нет релизов
        boolean allWithoutReleases = episodes.stream()
                .allMatch(e -> e.getReleases() == null || e.getReleases().isEmpty());
        assertTrue(allWithoutReleases, "All episodes should be without releases");
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_ShouldReturnEpisodesInCorrectOrder() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(seasonWithHalfReleases.getId());

        // Then
        assertNotNull(episodes);

        // Проверяем, что эпизоды отсортированы по номеру
        List<Integer> episodeNumbers = episodes.stream()
                .map(Episode::getNumber)
                .toList();

        boolean isSorted = IntStream.range(0, episodeNumbers.size() - 1)
                .allMatch(i -> episodeNumbers.get(i) <= episodeNumbers.get(i + 1));

        assertTrue(isSorted, "Episodes should be returned in order by number");
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_WhenSeasonDoesNotExist_ShouldReturnEmptyList() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(Long.MAX_VALUE);

        // Then
        assertNotNull(episodes);
        assertTrue(episodes.isEmpty(), "Should return empty list for non-existent season");
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_WhenSeasonHasNoEpisodes_ShouldReturnEmptyList() {
        // Given
        Series series = new SeriesBuilder().build();
        Series savedSeries = seriesRepository.save(series);

        Season emptySeason = new SeasonBuilder()
                .withNumber(99)
                .withEpisodes(new ArrayList<>())
                .build();
        emptySeason.setSeries(savedSeries);
        Season savedEmptySeason = seasonRepository.save(emptySeason);

        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(savedEmptySeason.getId());

        // Then
        assertNotNull(episodes);
        assertTrue(episodes.isEmpty(), "Should return empty list when season has no episodes");
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_ShouldNotLoadReleases() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(seasonWithHalfReleases.getId());

        // Then
        assertNotNull(episodes);
        assertFalse(episodes.isEmpty());

        // Проверяем, что для эпизодов без релизов коллекция releases пустая
        episodes.forEach(episode -> {
            assertTrue(
                    episode.getReleases() == null || episode.getReleases().isEmpty(),
                    "Episode " + episode.getNumber() + " should not have releases"
            );
        });
    }

    @Test
    void findEpisodesWithoutReleasesBySeasonId_ShouldReturnEpisodesWithCorrectProperties() {
        // When
        List<Episode> episodes = episodeRepository
                .findEpisodesWithoutReleasesBySeasonId(seasonWithoutReleases.getId());

        // Then
        assertNotNull(episodes);
        assertEquals(EPISODES_COUNT, episodes.size());

        // Проверяем, что у каждого эпизода есть ID и номер
        episodes.forEach(episode -> {
            assertNotNull(episode.getId(), "Episode should have an ID");
            assertNotNull(episode.getNumber(), "Episode should have a number");
            assertTrue(episode.getNumber() > 0, "Episode number should be positive");
        });
    }

    // Вспомогательные методы

    private Season createAndSaveSeason(Series series, int seasonNumber, int episodeCount, Boolean addReleases) {
        Season season = new SeasonBuilder()
                .withNumber(seasonNumber)
                .withEpisodes(createEpisodes(episodeCount, addReleases))
                .build();
        season.setSeries(series);
        return seasonRepository.save(season);
    }

    private List<Episode> createEpisodes(int count, Boolean addReleases) {
        List<Episode> episodes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int episodeNumber = i + 1;
            Episode episode = new EpisodeBuilder()
                    .withNumber(episodeNumber)
                    .withName("Episode " + episodeNumber)
                    .build();

            if (addReleases != null && (
                    (addReleases) ||
                            (!addReleases && episodeNumber % 2 == 0) // для половины релизов добавляем только четным
            )) {
                EpisodeRelease release = new EpisodeReleaseBuilder()
                        .withEpisode(episode)
                        .withSource(source)
                        .build();
                episode.setReleases(List.of(release));
            }

            episodes.add(episode);
        }

        return episodes;
    }
}