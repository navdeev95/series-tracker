package io.github.nikoir.series.tracker.content.domain;

import io.github.nikoir.series.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.SeasonRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
public class SeasonRepositoryTest {
    private static long seriesId1;
    private final static int SEASONS_COUNT1 = 5;
    private final static int EPISODES_COUNT1 = 10;


    private static long seriesId2;
    private final static int SEASONS_COUNT2 = 10;
    private final static int EPISODES_COUNT2 = 5;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @BeforeEach
    void setUp() {
        Series series1 = new SeriesBuilder()
                .withGeneratedSeasons(SEASONS_COUNT1, i -> new SeasonBuilder()
                        .withNumber(i + 1)
                        .withRandomEpisodes(EPISODES_COUNT1)
                        .build())
                .build();

        Series series2 = new SeriesBuilder()
                .withGeneratedSeasons(SEASONS_COUNT2, i -> new SeasonBuilder()
                        .withNumber(i + 1)
                        .withRandomEpisodes(EPISODES_COUNT2)
                        .build())
                .build();

        series1 = seriesRepository.save(series1);
        series2 = seriesRepository.save(series2);

        seriesId1 = series1.getId();
        seriesId2 = series2.getId();
    }

    @Test
    public void findBySeriesIdWithEpisodes_ForSeries1_ReturnsSeasons() {
        List<Season> seasonList = seasonRepository
                .findBySeriesIdWithEpisodes(seriesId1);

        assertEquals(SEASONS_COUNT1, seasonList.size());

        for (Season season: seasonList) {
            assertEquals(EPISODES_COUNT1, season.getEpisodes().size());
        }
    }

    @Test
    public void findBySeriesIdWithEpisodes_ForSeries2_ReturnsSeasons() {
        List<Season> seasonList = seasonRepository
                .findBySeriesIdWithEpisodes(seriesId2);

        assertEquals(SEASONS_COUNT2, seasonList.size());

        for (Season season: seasonList) {
            assertEquals(EPISODES_COUNT2, season.getEpisodes().size());
        }
    }


    @Test
    public void findBySeriesIdWithEpisodes_WithIncorrectId_ReturnsEmptyList() {
        List<Season> seasonList = seasonRepository
                .findBySeriesIdWithEpisodes(Math.max(seriesId1, seriesId2) + 1);
        assertEquals(0, seasonList.size());
    }

}
