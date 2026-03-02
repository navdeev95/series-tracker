package io.github.nikoir.series.tracker.content.service.sync;

import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.context.ExternalContentSearchStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesSyncService {
    private final ExternalContentSearchStrategyContext searchStrategyContext;
    private final SeriesContentSyncService contentSyncService;

    public SyncResult syncSeriesWithReleases(Series series) {
        Map<ExternalId, String> externalIds = ExternalId.mapExternalIds(series.getExternalIds());
        List<SeasonViewRs> externalSeasons = searchStrategyContext.search(externalIds);
        Source source = searchStrategyContext.getExternalContentSearchStrategy()
                .getDataSource();

        SyncResult result = contentSyncService.syncSeriesContent(series, externalSeasons, source);
        return result.hasNewContent() ? result : SyncResult.empty();
    }
}