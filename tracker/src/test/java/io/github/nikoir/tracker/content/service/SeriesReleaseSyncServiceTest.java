package io.github.nikoir.tracker.content.service;

import io.github.nikoir.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.tracker.builder.domain.SourceBuilder;
import io.github.nikoir.series.tracker.common.dto.response.EpisodeReleaseViewRs;
import io.github.nikoir.tracker.content.domain.entity.Episode;
import io.github.nikoir.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.tracker.content.domain.entity.Season;
import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.tracker.content.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.tracker.content.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.tracker.content.enums.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.github.nikoir.tracker.content.enums.Source.KINOPOISK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeriesReleaseSyncServiceTest {

    @Mock
    private SeasonEpisodeService seasonEpisodeService;

    @Mock
    private EpisodeReleaseRepository episodeReleaseRepository;

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private SeriesReleaseSyncService syncService;

    private Series series;
    private DictSource dictSource;

    private static final Source KINOPOISK_SOURCE = KINOPOISK;

    @BeforeEach
    void setUp() {
        DictExternalId kinopoiskId = new DictExternalId();
        kinopoiskId.setName(KINOPOISK_SOURCE.getName());

        series = new SeriesBuilder()
                .withExternalId(kinopoiskId, "123")
                .withTitle("Game of Thrones")
                .build();

        dictSource = new SourceBuilder(KINOPOISK_SOURCE)
                .withUrlTemplate("https://kinopoisk.ru/{id}")
                .build();

        when(sourceRepository.findByName(KINOPOISK_SOURCE.getName()))
                .thenReturn(Optional.of(dictSource));
    }

    @Test
    void syncReleases_WhenAllEpisodesAreMissing_ShouldCreateReleases() {
        // Given
        Season existingSeason = createSeason(1, 1, 2, 3);

        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 3);

        when(seasonEpisodeService.loadContentWithoutReleases(any()))
                .thenReturn(existingContent);

        // When
        List<EpisodeReleaseViewRs> result = syncService.syncReleases(series, List.of(externalSeason), KINOPOISK_SOURCE);

        // Then
        assertEquals(3, result.size());
        verify(seasonEpisodeService, never()).createSeasonsWithEpisodes(any(), anyList());
        verify(episodeReleaseRepository).saveAll(anyList());
    }

    @Test
    void syncReleases_WhenSomeEpisodesExist_ShouldCreateReleasesOnlyForMissing() {
        // Given
        Season existingSeason = createSeason(1, 2, 3);
        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 3);

        when(seasonEpisodeService.loadContentWithoutReleases(any()))
                .thenReturn(existingContent);

        // When
        List<EpisodeReleaseViewRs> result = syncService.syncReleases(series, List.of(externalSeason), KINOPOISK_SOURCE);

        // Then
        assertEquals(2, result.size()); // Episodes 1 and 2 are missing
        verify(seasonEpisodeService, never()).createSeasonsWithEpisodes(any(), anyList());

        ArgumentCaptor<List<EpisodeRelease>> releasesCaptor = ArgumentCaptor.forClass(List.class);
        verify(episodeReleaseRepository).saveAll(releasesCaptor.capture());
        List<EpisodeRelease> savedReleases = releasesCaptor.getValue();
        assertEquals(2, savedReleases.size());
    }

    @Test
    void syncReleases_WhenNoExternalSeasons_ShouldDoNothing() {
        // Given
        when(seasonEpisodeService.loadContentWithoutReleases(any()))
                .thenReturn(Collections.emptyList());

        // When
        List<EpisodeReleaseViewRs> result = syncService.syncReleases(series, Collections.emptyList(), KINOPOISK_SOURCE);

        // Then
        assertEquals(0, result.size());
        verify(seasonEpisodeService, never()).createSeasonsWithEpisodes(any(), anyList());
        verify(episodeReleaseRepository, never()).saveAll(anyList());
    }

    @Test
    void syncReleases_WhenSourceNotFound_ShouldThrowException() {
        // Given
        Season existingSeason = createSeason(1, 1);
        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 3);
        when(sourceRepository.findByName(KINOPOISK_SOURCE.getName()))
                .thenReturn(Optional.empty());

        when(seasonEpisodeService.loadContentWithoutReleases(any()))
                .thenReturn(existingContent);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            syncService.syncReleases(series, List.of(externalSeason), KINOPOISK_SOURCE);
        });
    }

    @Test
    void syncReleases_ShouldCreateReleasesWithCorrectSource() {
        // Given
        Season existingSeason = createSeason(1, 1, 2);
        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 2);

        when(seasonEpisodeService.loadContentWithoutReleases(any()))
                .thenReturn(existingContent);

        // When
        syncService.syncReleases(series, List.of(externalSeason), KINOPOISK_SOURCE);

        // Then
        ArgumentCaptor<List<EpisodeRelease>> releasesCaptor = ArgumentCaptor.forClass(List.class);
        verify(episodeReleaseRepository).saveAll(releasesCaptor.capture());

        List<EpisodeRelease> savedReleases = releasesCaptor.getValue();
        assertEquals(2, savedReleases.size());
        savedReleases.forEach(release -> {
            assertEquals(dictSource, release.getSource());
            assertNotNull(release.getEpisode());
        });
    }

    @Test
    void syncReleases_ShouldLogCorrectMessage() {
        // Given
        when(seasonEpisodeService.loadContentWithoutReleases(any()))
                .thenReturn(Collections.emptyList());

        // When
        List<EpisodeReleaseViewRs> result = syncService.syncReleases(series, Collections.emptyList(), KINOPOISK_SOURCE);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // Helper methods to create test data

    private List<SeasonInfo> createExternalSeasons(int count) {
        List<SeasonInfo> seasons = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            seasons.add(createExternalSeason(i, 5));
        }
        return seasons;
    }

    private SeasonInfo createExternalSeason(int seasonNumber, int episodeCount) {
        SeasonInfo seasonInfo = new SeasonInfo();
        seasonInfo.setNumber(seasonNumber);

        List<EpisodeInfo> episodes = new ArrayList<>();
        for (int i = 1; i <= episodeCount; i++) {
            EpisodeInfo episodeInfo = new EpisodeInfo();
            episodeInfo.setNumber(i);
            episodes.add(episodeInfo);
        }
        seasonInfo.setEpisodes(episodes);

        return seasonInfo;
    }

    private Season createSeason(int number, int... missingEpisodesNumbers) {
        Season season = new Season();
        season.setNumber(number);

        List<Episode> episodes = new ArrayList<>();
        for (int episodeNum: missingEpisodesNumbers) {
            episodes.add(new EpisodeBuilder().withNumber(episodeNum).build());
        }

        season.setEpisodes(episodes);
        return season;
    }
}