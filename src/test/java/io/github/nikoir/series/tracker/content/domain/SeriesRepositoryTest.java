package io.github.nikoir.series.tracker.content.domain;

import io.github.nikoir.series.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.series.tracker.builder.domain.EpisodeReleaseBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.content.domain.repo.specification.SeriesSpecifications;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.github.nikoir.series.tracker.content.domain.entity.Series.Status.COMPLETED;
import static io.github.nikoir.series.tracker.content.domain.entity.Series.Status.CONTINUING;
import static io.github.nikoir.series.tracker.content.enums.Source.MOVIELAB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SeriesRepositoryTest {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ExternalIdRepository externalIdRepository;

    @Autowired
    private SourceRepository sourceRepository;

    private Long seriesId1;
    private Long seriesId2;
    private Long seriesId3;

    private DictSource releaseSource;

    private DictExternalId kinopoiskId;
    private DictExternalId movielabId;


    private static final int EPISODES_COUNT = 10;

    @BeforeEach
    void setUp() {
        releaseSource = sourceRepository
                .findByName(MOVIELAB.getName())
                .orElseThrow();

        kinopoiskId = externalIdRepository.save(DictExternalId.builder()
                        .name(ExternalId.KINOPOISK.getName())
                .build());

        movielabId = externalIdRepository.save(DictExternalId.builder()
                .name(ExternalId.MOVIELAB.getName())
                .build());

        // Сериал 1: Game of Thrones
        Series series1 = new SeriesBuilder()
                .withTitle("Game of Thrones")
                .withEngTitle("Game of Thrones")
                .withStatus(COMPLETED)
                .withReleaseYear(2011)
                .withExternalId(kinopoiskId, "123")
                .withExternalId(movielabId, "456")
                .withGeneratedSeasons(5, i -> buildCompletedSeason(i + 1))
                .build();
        series1 = seriesRepository.save(series1);
        seriesId1 = series1.getId();

        // Сериал 2: House of the Dragon
        Series series2 = new SeriesBuilder()
                .withTitle("Дом Дракона")
                .withEngTitle("House of the Dragon")
                .withStatus(COMPLETED)
                .withSeason(buildCompletedSeason(1))
                .withSeason(buildCompletedSeason(2))
                .withSeason(buildUncompletedSeason(3, 8, 9, 10))
                .withExternalId(kinopoiskId, "1234")
                .withExternalId(movielabId, "5678")
                .withReleaseYear(2022)
                .build();
        series2 = seriesRepository.save(series2);
        seriesId2 = series2.getId();

        // Сериал 3: The Witcher (со статусом ANNOUNCED)
        Series series3 = new SeriesBuilder()
                .withTitle("Ведьмак")
                .withEngTitle("The Witcher")
                .withStatus(Series.Status.ANNOUNCED)
                .withReleaseYear(2019)
                .build();
        series3 = seriesRepository.save(series3);
        seriesId3 = series3.getId();
    }

    // ==================== Тесты для searchByTitleOrEngTitle ====================

    @Test
    public void searchByTitleOrEngTitle_WithMatchingTitle_ReturnsResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Series> result = seriesRepository.searchByTitleOrEngTitle("Game", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Game of Thrones", result.getContent().get(0).getTitle());
    }

    @Test
    public void searchByTitleOrEngTitle_WithMatchingEngTitle_ReturnsResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Series> result = seriesRepository.searchByTitleOrEngTitle("House", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("House of the Dragon", result.getContent().get(0).getEngTitle());
    }

    @Test
    public void searchByTitleOrEngTitle_WithPartialMatch_ReturnsResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Series> result = seriesRepository.searchByTitleOrEngTitle("Thrones", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Game of Thrones", result.getContent().get(0).getTitle());
    }

    @Test
    public void searchByTitleOrEngTitle_WithNonExistentTerm_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Series> result = seriesRepository.searchByTitleOrEngTitle("NonExistentSeries", pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    public void searchByTitleOrEngTitle_WithCaseInsensitiveSearch_ReturnsResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Series> result = seriesRepository.searchByTitleOrEngTitle("game", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Game of Thrones", result.getContent().get(0).getTitle());
    }

    @Test
    public void searchByTitleOrEngTitle_WithPagination_ReturnsCorrectPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Series> result = seriesRepository.searchByTitleOrEngTitle("the", pageable);

        // Должен найти House of the Dragon и The Witcher
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void searchSeriesSpecification_ExistingId_ReturnSeries() {
        Specification<Series> seriesSpecification = SeriesSpecifications
                .hasAnyExternalIdFromList(Map.of(ExternalId.KINOPOISK, "123"));

        List<Series> seriesList = seriesRepository
                .findAll(seriesSpecification)
                .stream()
                .toList();

        assertEquals(1, seriesList.size());

        assertEquals(seriesId1, seriesList.getFirst().getId());
    }

    @Test
    public void searchSeriesSpecification_unexistingId_ReturnEmptyList() {
        Specification<Series> seriesSpecification = SeriesSpecifications
                .hasAnyExternalIdFromList(Map.of(ExternalId.KINOPOISK, "456"));

        List<Series> seriesList = seriesRepository
                .findAll(seriesSpecification)
                .stream()
                .toList();

        assertTrue(seriesList.isEmpty());
    }

    @Test
    public void searchSeriesWithoutReleases_ShouldReturnSeries() {
        PageRequest page = PageRequest.of(0, 10);
        Page<Series> seriesResult = seriesRepository.searchSeriesWithoutReleases(page);

        //Should return House of the Dragon
        assertEquals(1, seriesResult.getContent().size());
        assertEquals(seriesId2, seriesResult.getContent().getFirst().getId());

        assertThat(seriesResult.getContent().getFirst().getExternalIds()).hasSize(2);
    }

    @Test
    public void searchSeriesWithCompletedSeasons_ShouldReturnSeries() {
        Series newSeries = new SeriesBuilder()
                .withTitle("Рик и Морти")
                .withEngTitle("Rick And Morty")
                .withStatus(Series.Status.CONTINUING)
                .withReleaseYear(2013)
                .withExternalId(kinopoiskId, "666")
                .withExternalId(movielabId, "666")
                .withGeneratedSeasons(5, i -> buildCompletedSeason(i + 1))
                .build();

        seriesRepository.save(newSeries);

        PageRequest page = PageRequest.of(0, 10);
        Page<Series> seriesPage = seriesRepository.searchSeriesWithCompletedSeasons(page, List.of(CONTINUING, COMPLETED));

        assertTrue(seriesPage.hasContent());
        assertEquals(2, seriesPage.getTotalElements());

        Series firstSeries = seriesPage.getContent().getFirst();
        Series secondSeries = seriesPage.getContent().get(1);

        assertEquals("Game of Thrones", firstSeries.getTitle());
        assertEquals("Рик и Морти", secondSeries.getTitle());

        assertThat(firstSeries.getExternalIds()).hasSize(2);
        assertThat(secondSeries.getExternalIds()).hasSize(2);
    }

    private Season buildUncompletedSeason(int number, int... episodesWithoutReleases) {
        return new SeasonBuilder()
                .withNumber(number)
                .withGeneratedEpisodes(EPISODES_COUNT, i -> {
                    if (Arrays.stream(episodesWithoutReleases).anyMatch(num -> num == i)) {
                        return buildEpisodeWithoutRelease(i + 1, LocalDate
                                .now()
                                .plusDays(1));
                    } else {
                        return buildEpisodeWithRelease(i + 1);
                    }

                })
                .build();
    }

    private Season buildCompletedSeason(int number) {
        return new SeasonBuilder()
                .withNumber(number)
                .withGeneratedEpisodes(EPISODES_COUNT, i -> buildEpisodeWithRelease(i + 1))
                .build();
    }

    private Episode buildEpisodeWithRelease(int number) {
        Episode episode = new EpisodeBuilder()
                .withNumber(number)
                .build();

        EpisodeRelease release = new EpisodeReleaseBuilder()
                .withEpisode(episode)
                .withSource(releaseSource)
                .build();

        episode.setReleases(List.of(release));

        return episode;
    }

    private Episode buildEpisodeWithoutRelease(int number, LocalDate releaseDate) {
        return new EpisodeBuilder()
                .withNumber(number)
                .withReleaseDate(releaseDate)
                .build();
    }
}