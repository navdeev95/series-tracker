package io.github.nikoir.series.tracker.content.domain;

import io.github.nikoir.series.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.series.tracker.builder.domain.EpisodeReleaseBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.SeasonRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.github.nikoir.series.tracker.content.enums.Source.MOVIELAB;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SeasonRepositoryTest {
    private final static int EPISODES_COUNT = 10;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private SourceRepository sourceRepository;

    private Series savedSeries;
    private DictSource source;

    @BeforeEach
    void setUp() {
        this.source = sourceRepository
                .findByName(MOVIELAB.getName())
                .orElseThrow();

        Series series = new SeriesBuilder()
                .withGeneratedSeasons(1, seasonsWithAllReleasesGeneratorFunction())
                .withGeneratedSeasons(1, seasonsWithHalfReleasesGeneratorFunction())
                .withGeneratedSeasons(1, seasonsWithoutReleasesGeneratorFunction())
                .build();

        savedSeries = seriesRepository.save(series);
    }

    @Test
    void findSeasonsWithEpisodesWithoutReleases_ShouldReturnSeasonsWithAtLeastOneEpisodeWithoutRelease() {
        // When
        List<Season> seasons = seasonRepository.findSeasonsWithEpisodesWithoutReleases(savedSeries.getId());

        // Then
        assertEquals(2, seasons.size(), "Should return seasons 2 (half releases) and 3 (no releases)");

        // Проверяем, что вернулись правильные сезоны
        List<Integer> seasonNumbers = seasons.stream()
                .map(Season::getNumber)
                .sorted()
                .toList();

        assertEquals(List.of(2, 3), seasonNumbers, "Should return seasons with numbers 2 and 3");

        // Проверяем, что сезон 1 (все эпизоды с релизами) не вернулся
        boolean hasSeasonWithAllReleases = seasons.stream()
                .anyMatch(s -> s.getNumber().equals(1));
        assertFalse(hasSeasonWithAllReleases, "Season with all releases should not be returned");
    }

    @Test
    void findSeasonsWithEpisodesWithoutReleases_ShouldReturnSeasonWithHalfReleases() {
        // When
        List<Season> seasons = seasonRepository.findSeasonsWithEpisodesWithoutReleases(savedSeries.getId());

        // Then
        Season halfReleasesSeason = seasons.stream()
                .filter(s -> s.getNumber().equals(2))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Season 2 should be present"));

        // Проверяем, что в сезоне есть эпизоды (метод возвращает сезоны с эпизодами)
        assertNotNull(halfReleasesSeason.getEpisodes());
        assertFalse(halfReleasesSeason.getEpisodes().isEmpty());

        // Проверяем, что все эпизоды в сезоне без релизов - только нечетные
        List<Integer> episodesWithoutReleases = halfReleasesSeason.getEpisodes().stream()
                .filter(e -> e.getReleases() == null || e.getReleases().isEmpty())
                .map(Episode::getNumber)
                .sorted()
                .toList();

        // Ожидаем нечетные номера: 1, 3, 5, 7, 9
        assertEquals(List.of(1, 3, 5, 7, 9), episodesWithoutReleases);
    }

    @Test
    void findSeasonsWithEpisodesWithoutReleases_ShouldReturnSeasonWithNoReleases() {
        // When
        List<Season> seasons = seasonRepository.findSeasonsWithEpisodesWithoutReleases(savedSeries.getId());

        // Then
        Season noReleasesSeason = seasons.stream()
                .filter(s -> s.getNumber().equals(3))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Season 3 should be present"));

        // Все эпизоды должны быть без релизов
        assertNotNull(noReleasesSeason.getEpisodes());
        assertEquals(EPISODES_COUNT, noReleasesSeason.getEpisodes().size());

        boolean allEpisodesWithoutReleases = noReleasesSeason.getEpisodes().stream()
                .allMatch(e -> e.getReleases() == null || e.getReleases().isEmpty());

        assertTrue(allEpisodesWithoutReleases, "All episodes in season 3 should be without releases");
    }

    @Test
    void findSeasonsWithEpisodesWithoutReleases_WhenAllEpisodesHaveReleases_ShouldReturnEmptyList() {
        // Given
        Series seriesWithAllReleases = new SeriesBuilder()
                .withGeneratedSeasons(1, seasonsWithAllReleasesGeneratorFunction())
                .build();
        Series savedSeriesWithAllReleases = seriesRepository.save(seriesWithAllReleases);

        // When
        List<Season> seasons = seasonRepository
                .findSeasonsWithEpisodesWithoutReleases(savedSeriesWithAllReleases.getId());

        // Then
        assertTrue(seasons.isEmpty(), "Should return empty list when all episodes have releases");
    }

    @Test
    void findSeasonsWithEpisodesWithoutReleases_WhenNoSeasonsExist_ShouldReturnEmptyList() {
        // Given
        Series seriesWithoutSeasons = new SeriesBuilder().build();
        Series savedSeriesWithoutSeasons = seriesRepository.save(seriesWithoutSeasons);

        // When
        List<Season> seasons = seasonRepository
                .findSeasonsWithEpisodesWithoutReleases(savedSeriesWithoutSeasons.getId());

        // Then
        assertTrue(seasons.isEmpty(), "Should return empty list when series has no seasons");
    }

    @Test
    void findSeasonsWithEpisodesWithoutReleases_ShouldLoadEpisodesEagerly() {
        // When
        List<Season> seasons = seasonRepository
                .findSeasonsWithEpisodesWithoutReleases(savedSeries.getId());

        // Then
        assertFalse(seasons.isEmpty());

        // Проверяем, что эпизоды загружены (не LazyInitializationException)
        Season firstSeason = seasons.get(0);
        assertDoesNotThrow(() -> {
            int episodeCount = firstSeason.getEpisodes().size();
            assertTrue(episodeCount > 0);
        });
    }

    // Вспомогательные методы для генерации тестовых данных

    private Function<Integer, Season> seasonsWithAllReleasesGeneratorFunction() {
        return i -> new SeasonBuilder()
                .withNumber(1)
                .withGeneratedEpisodes(EPISODES_COUNT, episodeGeneratorFunction(number -> true))
                .build();
    }

    private Function<Integer, Season> seasonsWithHalfReleasesGeneratorFunction() {
        return i -> new SeasonBuilder()
                .withNumber(2)
                .withGeneratedEpisodes(EPISODES_COUNT, episodeGeneratorFunction(number -> number % 2 == 0))
                .build();
    }

    private Function<Integer, Season> seasonsWithoutReleasesGeneratorFunction() {
        return i -> new SeasonBuilder()
                .withNumber(3)
                .withGeneratedEpisodes(EPISODES_COUNT, episodeGeneratorFunction(number -> false))
                .build();
    }

    private Function<Integer, Episode> episodeGeneratorFunction(Predicate<Integer> addReleaseCondition) {
        return i -> {
            int number = i + 1;
            Episode episode = new EpisodeBuilder()
                    .withNumber(number)
                    .withName("Episode " + number)
                    .build();

            if (addReleaseCondition.test(number)) {
                episode.setReleases(List.of(
                        new EpisodeReleaseBuilder()
                                .withEpisode(episode)
                                .withSource(source)
                                .build()
                ));
            }

            return episode;
        };
    }
}