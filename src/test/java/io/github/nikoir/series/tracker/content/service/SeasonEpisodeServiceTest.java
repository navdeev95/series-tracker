package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.builder.domain.EpisodeBuilder;
import io.github.nikoir.series.tracker.builder.domain.SeasonBuilder;
import io.github.nikoir.series.tracker.builder.dto.SeasonViewRsBuilder;
import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SeasonEpisodeServiceTest {
    private Season existingSeason;
    private SeasonViewRs foundSeason;

    @InjectMocks
    private SeasonEpisodeService seasonEpisodeService;

    @BeforeEach
    void setUp() {
        SeasonBuilder seasonBuilder = new SeasonBuilder();
        existingSeason = seasonBuilder
                .withRandomEpisodes(10)
                .build();

        SeasonViewRsBuilder seasonViewBuilder = new SeasonViewRsBuilder();
        foundSeason = seasonViewBuilder.fromSeason(existingSeason);
    }

    @Test
    public void findMissingEpisodes_NoDifference_ReturnsEmpty() {
        List<SeasonViewRs.EpisodeViewRs> episodes = seasonEpisodeService.findMissingEpisodes(existingSeason, foundSeason);
        assertTrue(episodes.isEmpty());
    }

    @Test
    public void findMissingEpisodes_WithExtraEpisodesInFound_ReturnsExtraEpisodes() {
        List<Episode> removedEpisodes = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            removedEpisodes.add(existingSeason.getEpisodes().removeLast());
        }

        List<SeasonViewRs.EpisodeViewRs> newEpisodes = seasonEpisodeService.findMissingEpisodes(existingSeason, foundSeason);
        assertEquals(removedEpisodes.size(), newEpisodes.size());

        removedEpisodes.sort(Comparator.comparingInt(Episode::getNumber));
        newEpisodes = newEpisodes.stream().sorted(Comparator.comparingInt(SeasonViewRs.EpisodeViewRs::number)).toList();

        for (int i = 0; i < newEpisodes.size(); i++) {
            assertEpisodes(removedEpisodes.get(i), newEpisodes.get(i));
        }
    }

    @Test
    public void findMissingEpisodes_WithExtraEpisodesInExisting_ReturnsEmpty() {
        EpisodeBuilder builder = new EpisodeBuilder();

        int lastEpisodeNumber = existingSeason.getEpisodes().stream()
                .mapToInt(Episode::getNumber)
                .max()
                .orElse(0);

        for (int i = 1; i <= 3; i++) {
            existingSeason.getEpisodes().add(builder
                    .withNumber(lastEpisodeNumber + i)
                    .build());
        }

        List<SeasonViewRs.EpisodeViewRs> foundEpisodes = seasonEpisodeService.findMissingEpisodes(existingSeason, foundSeason);
        assertTrue(foundEpisodes.isEmpty());
    }

    private void assertEpisodes(Episode episode, SeasonViewRs.EpisodeViewRs episodeViewRs) {
        assertEquals(episode.getName(), episodeViewRs.name());
        assertEquals(episode.getNumber(), episodeViewRs.number());
        assertEquals(Date.from(episode.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)), episodeViewRs.releaseDate());
    }

}
