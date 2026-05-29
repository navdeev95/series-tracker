package io.github.nikoir.series.tracker.content.domain;

import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.series.tracker.content.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SeriesRepositoryTest {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ExternalIdRepository externalIdRepository;

    private Long seriesId1;
    private Long seriesId2;
    private Long seriesId3;

    @BeforeEach
    void setUp() {
        DictExternalId kinopoiskId = externalIdRepository.save(DictExternalId.builder()
                        .name(ExternalId.KINOPOISK.getName())
                .build());

        DictExternalId movielabId = externalIdRepository.save(DictExternalId.builder()
                .name(ExternalId.MOVIELAB.getName())
                .build());

        // Сериал 1: Game of Thrones
        Series series1 = new SeriesBuilder()
                .withTitle("Game of Thrones")
                .withEngTitle("Game of Thrones")
                .withStatus(Series.Status.COMPLETED)
                .withReleaseYear(2011)
                .withExternalId(kinopoiskId, "123")
                .withExternalId(movielabId, "456")
                .build();
        series1 = seriesRepository.save(series1);
        seriesId1 = series1.getId();

        // Сериал 2: House of the Dragon
        Series series2 = new SeriesBuilder()
                .withTitle("Дом Дракона")
                .withEngTitle("House of the Dragon")
                .withStatus(Series.Status.COMPLETED)
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

    // ==================== Тесты для searchSeriesWithStatus ====================

    @Test
    public void searchSeriesWithStatus_IncludeUnknownStatusTrue_ExcludesSpecifiedStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Series.Status> excludedStatus = List.of(Series.Status.ANNOUNCED);

        Page<Series> result = seriesRepository.searchSeriesWithStatus(true, excludedStatus, pageable);

        // Должен вернуть: Game of Thrones (COMPLETED) и House of the Dragon (COMPLETED)
        // The Witcher (ANNOUNCED) исключен, но так как unknownStatus=true,
        // сериалов с null статусом нет, поэтому только 2
        assertEquals(2, result.getTotalElements());

        for (Series series : result.getContent()) {
            assertSame(Series.Status.COMPLETED, series.getStatus());
        }
    }

    @Test
    public void searchSeriesWithStatus_IncludeUnknownStatusFalse_ExcludesSpecifiedStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Series.Status> excludedStatus = List.of(Series.Status.ANNOUNCED);

        Page<Series> result = seriesRepository.searchSeriesWithStatus(false, excludedStatus, pageable);

        // Должен вернуть только RELEASED сериалы
        assertEquals(2, result.getTotalElements());

        for (Series series : result.getContent()) {
            assertEquals(Series.Status.COMPLETED, series.getStatus());
        }
    }

    @Test
    public void searchSeriesWithStatus_ExcludeMultipleStatuses_ReturnsCorrectResults() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Series.Status> excludedStatus = List.of(Series.Status.COMPLETED, Series.Status.ANNOUNCED);

        Page<Series> result = seriesRepository.searchSeriesWithStatus(true, excludedStatus, pageable);

        // Все статусы исключены, сериалов с null статусом нет
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    public void searchSeriesWithStatus_EmptyExcludedList_ReturnsAllSeries() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Series.Status> excludedStatus = List.of();

        Page<Series> result = seriesRepository.searchSeriesWithStatus(true, excludedStatus, pageable);

        // Должен вернуть все 3 сериала
        assertEquals(3, result.getTotalElements());
    }

    @Test
    public void searchSeriesWithStatus_IncludeUnknownStatusTrue_WithNullStatusSeries() {
        // Создаем сериал с null статусом
        Series seriesWithNullStatus = new SeriesBuilder()
                .withTitle("Series Without Status")
                .withEngTitle("Series Without Status")
                .withStatus(null)  // Явно устанавливаем null
                .withReleaseYear(2023)
                .build();
        seriesRepository.save(seriesWithNullStatus);

        Pageable pageable = PageRequest.of(0, 10);
        List<Series.Status> excludedStatus = List.of(Series.Status.ANNOUNCED);

        Page<Series> result = seriesRepository.searchSeriesWithStatus(true, excludedStatus, pageable);

        // Должен вернуть: Game of Thrones (RELEASED), House of the Dragon (RELEASED)
        // и сериал с null статусом
        assertEquals(3, result.getTotalElements());

        boolean hasNullStatus = result.getContent().stream()
                .anyMatch(series -> series.getStatus() == null);
        assertTrue(hasNullStatus);
    }

    @Test
    public void searchSeriesWithStatus_IncludeUnknownStatusFalse_WithNullStatusSeries() {
        // Создаем сериал с null статусом
        Series seriesWithNullStatus = new SeriesBuilder()
                .withTitle("Series Without Status")
                .withEngTitle("Series Without Status")
                .withStatus(null)
                .withReleaseYear(2023)
                .build();
        seriesRepository.save(seriesWithNullStatus);

        Pageable pageable = PageRequest.of(0, 10);
        List<Series.Status> excludedStatus = List.of(Series.Status.ANNOUNCED);

        Page<Series> result = seriesRepository.searchSeriesWithStatus(false, excludedStatus, pageable);

        // Должен вернуть только RELEASED сериалы (без сериала с null статусом)
        assertEquals(2, result.getTotalElements());

        for (Series series : result.getContent()) {
            assertEquals(Series.Status.COMPLETED, series.getStatus());
        }
    }

    @Test
    public void searchSeriesWithStatus_WithPagination_ReturnsCorrectPage() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Series.Status> excludedStatus = List.of();

        Page<Series> result = seriesRepository.searchSeriesWithStatus(true, excludedStatus, pageable);

        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        // Проверяем, что сериалы отсортированы по id ASC
        assertEquals(seriesId1, result.getContent().get(0).getId());
        assertEquals(seriesId2, result.getContent().get(1).getId());
    }

    @Test
    public void searchSeriesWithStatus_SecondPage_ReturnsRemainingSeries() {
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        List<Series.Status> excludedStatus = List.of();

        Page<Series> firstPageResult = seriesRepository.searchSeriesWithStatus(true, excludedStatus, firstPage);
        Page<Series> secondPageResult = seriesRepository.searchSeriesWithStatus(true, excludedStatus, secondPage);

        assertEquals(3, firstPageResult.getTotalElements());
        assertEquals(2, firstPageResult.getContent().size());
        assertEquals(1, secondPageResult.getContent().size());

        // Проверяем, что на второй странице остался последний сериал
        assertEquals(seriesId3, secondPageResult.getContent().get(0).getId());
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
}