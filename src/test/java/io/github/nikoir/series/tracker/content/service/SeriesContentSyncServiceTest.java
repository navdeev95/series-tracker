package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.builder.domain.SourceBuilder;
import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeReleaseRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SourceRepository;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.service.sync.SeriesContentSyncService;
import io.github.nikoir.series.tracker.factory.EpisodeTestFactory;
import io.github.nikoir.series.tracker.factory.SeasonTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static io.github.nikoir.series.tracker.content.enums.Source.KINOPOISK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeriesContentSyncServiceTest {
    @Mock
    private SeasonEpisodeService seasonEpisodeService;

    @Mock
    private EpisodeReleaseRepository episodeReleaseRepository;

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private SeriesContentSyncService contentSyncService;

    private Series series;

    private Season existingSeason;

    @BeforeEach
    void setUp() {
        SeasonBuilder seasonBuilder = new SeasonBuilder();
        SeriesBuilder seriesBuilder = new SeriesBuilder();

        series = seriesBuilder.withSeason(seasonBuilder
                .withRandomEpisodes(10)
                .build()).build();

        existingSeason = series.getSeasons().getFirst();

        when(seasonEpisodeService.loadCurrentContent(series.getId())).thenReturn(series.getSeasons());
    }


    @Test
    public void syncSeriesContent_NewEpisodes_CreateNewEpisodes() {
        SeasonViewRs externalSeason = SeasonTestFactory.fromSeason(existingSeason);
        int maxEpisodeNumber = getMaxEpisodeNumber(existingSeason);
        int newEpisodesCnt = 3;
        List<SeasonViewRs.EpisodeViewRs> newEpisodes = createNewEpisodes(maxEpisodeNumber, newEpisodesCnt);

        SourceBuilder sourceBuilder = new SourceBuilder(KINOPOISK);

        Optional<DictSource> optionalSource = Optional
                .ofNullable(sourceBuilder.build());

        when(sourceRepository.findByName(anyString()))
                .thenReturn(optionalSource);

        when(seasonEpisodeService.findMissingEpisodes(any(Season.class), any(SeasonViewRs.class)))
                .thenReturn(newEpisodes);

        when(seasonEpisodeService.createEpisodes(any(Season.class), anyList()))
                .thenReturn(EpisodeTestFactory.fromEpisodeViewList(newEpisodes));

        SyncResult syncResult = contentSyncService.syncSeriesContent(series.getId(),
                List.of(externalSeason),
                KINOPOISK);

        assertEquals(0, syncResult.getNewSeasonsCnt());
        assertEquals(newEpisodesCnt, syncResult.getNewEpisodesCnt());
        assertTrue(syncResult.hasNewContent());
    }

    @Test
    public void syncSeriesContent_NewSeason_CreateNewSeason() {
        Season nonExistingSeason = createNewSeason(existingSeason.getNumber() + 1, 10);

        SeasonViewRs firstExternalSeason = SeasonTestFactory.fromSeason(existingSeason);
        SeasonViewRs secondExternalSeason = SeasonTestFactory.fromSeason(nonExistingSeason);

        SourceBuilder sourceBuilder = new SourceBuilder(KINOPOISK);

        Optional<DictSource> optionalSource = Optional
                .ofNullable(sourceBuilder.build());

        when(sourceRepository.findByName(anyString()))
                .thenReturn(optionalSource);

        when(seasonEpisodeService.createSeason(eq(series.getId()), any(SeasonViewRs.class)))
                .thenReturn(nonExistingSeason);

        SyncResult result = contentSyncService.syncSeriesContent(series.getId(),
                List.of(firstExternalSeason, secondExternalSeason),
                KINOPOISK);

        assertEquals(1, result.getNewSeasonsCnt());
        assertEquals(nonExistingSeason.getEpisodes().size(),
                result.getNewEpisodesCnt());
        assertTrue(result.hasNewContent());

    }

    @Test
    public void syncSeriesContent_NoNewData_ReturnEmptyResult() {
        SeasonViewRs externalSeason = SeasonTestFactory.fromSeason(existingSeason);

        when(seasonEpisodeService.findMissingEpisodes(any(Season.class), any(SeasonViewRs.class)))
                .thenReturn(Collections.emptyList());

        SyncResult result = contentSyncService.syncSeriesContent(series.getId(),
                List.of(externalSeason),
                KINOPOISK);

        verify(seasonEpisodeService, never()).createSeason(anyLong(), any(SeasonViewRs.class));
        verify(seasonEpisodeService, never()).createEpisodes(any(Season.class), anyList());
        verify(episodeReleaseRepository, never()).saveAll(anyList());

        assertEquals(0, result.getNewEpisodesCnt());
        assertEquals(0, result.getNewSeasonsCnt());
        assertFalse(result.hasNewContent());
    }

    private int getMaxEpisodeNumber(Season existingSeason) {
        return existingSeason
                .getEpisodes()
                .stream()
                .mapToInt(Episode::getNumber)
                .max()
                .orElse(0);
    }

    private List<SeasonViewRs.EpisodeViewRs> createNewEpisodes(int maxEpisodeNumber, int newEpisodesCnt) {
        EpisodeBuilder episodeBuilder = new EpisodeBuilder();

        List<SeasonViewRs.EpisodeViewRs> newEpisodes = new LinkedList<>();
        for (int i = 1; i <= newEpisodesCnt; i++) {
            newEpisodes.add(EpisodeTestFactory
                    .fromEpisode(episodeBuilder
                            .withNumber(maxEpisodeNumber + i)
                            .build()));
        }

        return newEpisodes;
    }

    private Season createNewSeason(int newSeasonNumber, int episodesCnt) {
        SeasonBuilder seasonBuilder = new SeasonBuilder();

        return seasonBuilder
                .withRandomEpisodes(episodesCnt)
                .withSeries(series)
                .withNumber(newSeasonNumber)
                .build();
    }
}
