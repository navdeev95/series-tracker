package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.EpisodeRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeasonRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.mapper.EpisodeMapper;
import io.github.nikoir.series.tracker.content.mapper.SeasonMapper;
import io.github.nikoir.series.tracker.factory.SeasonTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeasonEpisodeServiceTest {
    private final static int SEASONS_COUNT = 5;
    private final static int EPISODES_COUNT = 10;

    private Series existingSeries;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private SeasonMapper seasonMapper;

    @Mock
    private EpisodeMapper episodeMapper;

    @Mock
    private EpisodeRepository episodeRepository;

    @InjectMocks
    private SeasonEpisodeService seasonEpisodeService;

    @BeforeEach
    void setUp() {
        existingSeries = new Series();
        existingSeries.setId(1L);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, EPISODES_COUNT})
    void createSeasonsWithEpisodes_ShouldCreateMultipleSeasons(int episodesCount) {
        // Given
        mockInjectionsForCreation();

        List<Season> seasonsWithEpisodes = getSeasonsWithEpisodes(episodesCount);
        existingSeries.setSeasons(seasonsWithEpisodes);

        when(seasonRepository.saveAll(anyList())).thenReturn(seasonsWithEpisodes);
        List<SeasonInfo> seasonInfos = SeasonTestFactory.fromSeasons(seasonsWithEpisodes);

        // When
        seasonEpisodeService.createSeasonsWithEpisodes(existingSeries.getId(), seasonInfos);

        // Then
        ArgumentCaptor<List<Season>> seasonsCaptor = ArgumentCaptor.forClass(List.class);
        verify(seasonRepository).saveAll(seasonsCaptor.capture());

        List<Season> savedSeasons = seasonsCaptor.getValue();
        assertThat(savedSeasons).hasSize(SEASONS_COUNT);

        for (Season season : savedSeasons) {
            assertThat(season.getSeries()).isEqualTo(existingSeries);
            assertThat(season.getEpisodes()).hasSize(episodesCount);
        }
    }

    @Test
    void createSeasonsWithEpisodes_WhenEpisodesNull_ShouldCreateSeasonsWithEmptyEpisodes() {
        //Given
        mockInjectionsForCreation();
        List<Season> seasonWithNullEpisodes = getSeasonsWithNullEpisodes();
        existingSeries.setSeasons(seasonWithNullEpisodes);

        List<SeasonInfo> seasonInfos = SeasonTestFactory.fromSeasons(seasonWithNullEpisodes);

        seasonEpisodeService.createSeasonsWithEpisodes(existingSeries.getId(), seasonInfos);

        ArgumentCaptor<List<Season>> seasonsCaptor = ArgumentCaptor.forClass(List.class);
        verify(seasonRepository).saveAll(seasonsCaptor.capture());

        List<Season> savedSeasons = seasonsCaptor.getValue();
        assertThat(savedSeasons).hasSize(SEASONS_COUNT);

        for (Season season : savedSeasons) {
            assertThat(season.getSeries()).isEqualTo(existingSeries);
            assertThat(season.getEpisodes()).isEmpty();
        }
    }

    @Test
    void loadContentWithoutReleases_ShouldReturnSeasonsWithEpisodesWithoutReleases() {
        // Given
        Season season = new SeasonBuilder()
                .withNumber(1)
                .withRandomEpisodes(2)
                .build();

        when(seasonRepository.findSeasonsWithEpisodesWithoutReleases(existingSeries.getId()))
                .thenReturn(List.of(season));
        when(episodeRepository.findEpisodesWithoutReleasesBySeasonId(season.getId()))
                .thenReturn(season.getEpisodes());

        // When
        List<Season> result = seasonEpisodeService.loadContentWithoutReleases(existingSeries.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEpisodes()).hasSize(2);
        verify(episodeRepository).findEpisodesWithoutReleasesBySeasonId(season.getId());
    }

    @Test
    void loadContentWithoutReleases_WhenNoSeasonsFound_ShouldReturnEmptyList() {
        // Given
        when(seasonRepository.findSeasonsWithEpisodesWithoutReleases(existingSeries.getId()))
                .thenReturn(Collections.emptyList());

        // When
        List<Season> result = seasonEpisodeService.loadContentWithoutReleases(existingSeries.getId());

        // Then
        assertThat(result).isEmpty();
        verify(episodeRepository, never()).findEpisodesWithoutReleasesBySeasonId(any());
    }

    @Test
    void loadContentWithoutReleases_WhenNoEpisodesFound_ShouldReturnSeasonWithoutEpisodes() {
        // Given
        Season season = new SeasonBuilder()
                .withNumber(1)
                .withRandomEpisodes(1)
                .build();

        when(seasonRepository.findSeasonsWithEpisodesWithoutReleases(existingSeries.getId()))
                .thenReturn(List.of(season));
        when(episodeRepository.findEpisodesWithoutReleasesBySeasonId(season.getId()))
                .thenReturn(Collections.emptyList());

        // When
        List<Season> result = seasonEpisodeService.loadContentWithoutReleases(existingSeries.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEpisodes()).isEmpty();
    }

    private void mockInjectionsForCreation() {
        when(seriesRepository.getReferenceById(existingSeries.getId())).thenReturn(existingSeries);

        when(seasonMapper.toEntity(any(SeasonInfo.class))).thenAnswer(invocation -> {
            SeasonInfo info = invocation.getArgument(0);
            return new SeasonBuilder()
                    .withNumber(info.getNumber())
                    .withName(info.getName())
                    .withReleaseDate(info.getReleaseDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())
                    .build();
        });

        when(episodeMapper.toEntities(anyList())).thenAnswer(invocation -> {
            List<EpisodeInfo> episodeInfos = invocation.getArgument(0);
            return episodeInfos.stream()
                    .map(info -> new EpisodeBuilder()
                            .withName(info.getName())
                            .withNumber(info.getNumber())
                            .withReleaseDate(info.getReleaseDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate())
                            .build())
                    .toList();
        });
    }

    private List<Season> getSeasonsWithEpisodes(int episodesCount) {
        List<Season> existingSeasons = new ArrayList<>();

        for (int i = 0; i < SEASONS_COUNT; i++) {
            Season seasonToAdd = new SeasonBuilder()
                    .withRandomEpisodes(episodesCount)
                    .withSeries(existingSeries)
                    .withNumber(i + 1)
                    .build();

            seasonToAdd.getEpisodes().forEach(e -> e.setSeason(seasonToAdd));

            existingSeasons.add(seasonToAdd);
        }

        return existingSeasons;
    }

    private List<Season> getSeasonsWithNullEpisodes() {
        List<Season> existingSeasons = new ArrayList<>();

        for (int i = 0; i < SEASONS_COUNT; i++) {
            existingSeasons.add(new SeasonBuilder()
                    .withEpisodes(null)
                    .withSeries(existingSeries)
                    .withNumber(i + 1)
                    .build());
        }

        return existingSeasons;
    }

}
