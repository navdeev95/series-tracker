package io.github.nikoir.series.tracker.content.service.sync;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.context.ExternalContentSearchStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesSyncService {
    private final ExternalContentSearchStrategyContext searchStrategyContext;
    private final SeriesContentSyncService contentSyncService;

    public SyncResult syncSeriesWithReleases(SeriesDetailViewRs seriesDetails) {
        List<SeasonViewRs> externalSeasons = searchStrategyContext.search(seriesDetails.externalIds());
        Source source = searchStrategyContext.getExternalContentSearchStrategy()
                .getDataSource();

        SyncResult result = contentSyncService.syncSeriesContent(seriesDetails.id(), externalSeasons, source);
        return result.hasNewContent() ? result : SyncResult.empty();
    }
}