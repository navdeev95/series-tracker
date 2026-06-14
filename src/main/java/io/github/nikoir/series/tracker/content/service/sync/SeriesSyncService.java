package io.github.nikoir.series.tracker.content.service.sync;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.impl.MovieLabEpisodeSearchStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesSyncService {
    private final MovieLabEpisodeSearchStrategy episodeSearchStrategy;
    private final SeriesContentSyncService contentSyncService;

    public SyncResult syncSeriesWithReleases(SeriesDetailViewRs seriesDetails) {
        List<SeasonViewRs> externalSeasons = episodeSearchStrategy.searchEpisodes(seriesDetails.getExternalIds());
        Source source = episodeSearchStrategy.getDataSource();

        SyncResult result = contentSyncService.syncSeriesContent(seriesDetails.getInnerId(), externalSeasons, source);
        return result.hasNewContent() ? result : SyncResult.empty();
    }
}