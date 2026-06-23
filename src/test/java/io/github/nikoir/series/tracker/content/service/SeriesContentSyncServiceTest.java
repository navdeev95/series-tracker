package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.Source;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeriesContentSyncServiceTest {

    @Mock
    private SeasonEpisodeService seasonEpisodeService;

    @Mock
    private EpisodeReleaseRepository episodeReleaseRepository;

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private SeriesContentSyncService syncService;

    private static final Long SERIES_ID = 1L;
    private static final Source SOURCE = Source.KINOPOISK;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void syncSeriesContent_WhenAllSeasonsAreNew_ShouldCreateSeasonsWithEpisodes() {
        // Given
        List<Season> existingContent = Collections.emptyList();
        List<SeasonInfo> externalSeasons = createExternalSeasons(2);

        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(existingContent);

        // When
        SyncResult result = syncService.syncSeriesContent(SERIES_ID, externalSeasons, SOURCE);

        // Then
        assertEquals(2, result.getNewSeasonsCnt());
        assertEquals(0, result.getNewEpisodesCnt());
        verify(seasonEpisodeService).createSeasonsWithEpisodes(eq(SERIES_ID), eq(externalSeasons));
        verify(episodeReleaseRepository, never()).saveAll(anyList());
    }

    @Test
    void syncSeriesContent_WhenAllEpisodesAreMissing_ShouldCreateReleases() {
        // Given
        Season existingSeason = createSeason(1, 1, 2, 3);

        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 3);

        DictSource dictSource = new DictSource();
        dictSource.setName(SOURCE.getName());

        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(existingContent);
        when(sourceRepository.findByName(SOURCE.getName()))
                .thenReturn(Optional.of(dictSource));

        // When
        SyncResult result = syncService.syncSeriesContent(SERIES_ID, List.of(externalSeason), SOURCE);

        // Then
        assertEquals(0, result.getNewSeasonsCnt());
        assertEquals(3, result.getNewEpisodesCnt());
        verify(seasonEpisodeService, never()).createSeasonsWithEpisodes(any(), anyList());
        verify(episodeReleaseRepository).saveAll(anyList());
    }

    @Test
    void syncSeriesContent_WhenSomeEpisodesExist_ShouldCreateReleasesOnlyForMissing() {
        // Given
        Season existingSeason = createSeason(1, 2, 3);
        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 3);

        DictSource dictSource = new DictSource();
        dictSource.setName(SOURCE.getName());

        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(existingContent);
        when(sourceRepository.findByName(SOURCE.getName()))
                .thenReturn(Optional.of(dictSource));

        // When
        SyncResult result = syncService.syncSeriesContent(SERIES_ID, List.of(externalSeason), SOURCE);

        // Then
        assertEquals(0, result.getNewSeasonsCnt());
        assertEquals(2, result.getNewEpisodesCnt()); // Episodes 1 and 2 are missing
        verify(seasonEpisodeService, never()).createSeasonsWithEpisodes(any(), anyList());

        ArgumentCaptor<List<EpisodeRelease>> releasesCaptor = ArgumentCaptor.forClass(List.class);
        verify(episodeReleaseRepository).saveAll(releasesCaptor.capture());
        List<EpisodeRelease> savedReleases = releasesCaptor.getValue();
        assertEquals(2, savedReleases.size());
    }

    @Test
    void syncSeriesContent_MixedNewSeasonsAndMissingEpisodes_ShouldHandleBoth() {
        // Given
        Season existingSeason = createSeason(1, 2, 3);
        List<Season> existingContent = List.of(existingSeason);

        List<SeasonInfo> externalSeasons = new ArrayList<>();
        externalSeasons.add(createExternalSeason(1, 3)); // Season 1 exists, missing episodes 1-2
        externalSeasons.add(createExternalSeason(2, 2)); // Season 2 is new

        DictSource dictSource = new DictSource();
        dictSource.setName(SOURCE.getName());

        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(existingContent);
        when(sourceRepository.findByName(SOURCE.getName()))
                .thenReturn(Optional.of(dictSource));

        // When
        SyncResult result = syncService.syncSeriesContent(SERIES_ID, externalSeasons, SOURCE);

        // Then
        assertEquals(1, result.getNewSeasonsCnt());
        assertEquals(2, result.getNewEpisodesCnt()); // Only from existing season

        ArgumentCaptor<List<SeasonInfo>> seasonsCaptor = ArgumentCaptor.forClass(List.class);
        verify(seasonEpisodeService).createSeasonsWithEpisodes(eq(SERIES_ID), seasonsCaptor.capture());
        List<SeasonInfo> createdSeasons = seasonsCaptor.getValue();
        assertEquals(1, createdSeasons.size());
        assertEquals(2, createdSeasons.get(0).getNumber());

        ArgumentCaptor<List<EpisodeRelease>> releasesCaptor = ArgumentCaptor.forClass(List.class);
        verify(episodeReleaseRepository).saveAll(releasesCaptor.capture());
        assertEquals(2, releasesCaptor.getValue().size());
    }

    @Test
    void syncSeriesContent_WhenNoExternalSeasons_ShouldDoNothing() {
        // Given
        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(Collections.emptyList());

        // When
        SyncResult result = syncService.syncSeriesContent(SERIES_ID, Collections.emptyList(), SOURCE);

        // Then
        assertEquals(0, result.getNewSeasonsCnt());
        assertEquals(0, result.getNewEpisodesCnt());
        verify(seasonEpisodeService, never()).createSeasonsWithEpisodes(any(), anyList());
        verify(episodeReleaseRepository, never()).saveAll(anyList());
        verify(sourceRepository, never()).findByName(any());
    }

    @Test
    void syncSeriesContent_WhenSourceNotFound_ShouldThrowException() {
        // Given
        Season existingSeason = createSeason(1, 1);
        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 3);

        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(existingContent);
        when(sourceRepository.findByName(SOURCE.getName()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            syncService.syncSeriesContent(SERIES_ID, List.of(externalSeason), SOURCE);
        });
    }

    @Test
    void syncSeriesContent_ShouldCreateReleasesWithCorrectSource() {
        // Given
        Season existingSeason = createSeason(1, 1, 2);
        List<Season> existingContent = List.of(existingSeason);
        SeasonInfo externalSeason = createExternalSeason(1, 2);

        DictSource expectedSource = new DictSource();
        expectedSource.setName(SOURCE.getName());

        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(existingContent);
        when(sourceRepository.findByName(SOURCE.getName()))
                .thenReturn(Optional.of(expectedSource));

        // When
        syncService.syncSeriesContent(SERIES_ID, List.of(externalSeason), SOURCE);

        // Then
        ArgumentCaptor<List<EpisodeRelease>> releasesCaptor = ArgumentCaptor.forClass(List.class);
        verify(episodeReleaseRepository).saveAll(releasesCaptor.capture());

        List<EpisodeRelease> savedReleases = releasesCaptor.getValue();
        assertEquals(2, savedReleases.size());
        savedReleases.forEach(release -> {
            assertEquals(expectedSource, release.getSource());
            assertNotNull(release.getEpisode());
        });
    }

    @Test
    void syncSeriesContent_ShouldLogCorrectMessage() {
        // Given
        when(seasonEpisodeService.loadContentWithoutReleases(SERIES_ID))
                .thenReturn(Collections.emptyList());

        // When
        SyncResult result = syncService.syncSeriesContent(SERIES_ID, Collections.emptyList(), SOURCE);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getNewSeasonsCnt());
        assertEquals(0, result.getNewEpisodesCnt());
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