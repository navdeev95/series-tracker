package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.dto.external.response.SeasonViewRs;
import io.github.nikoir.series.tracker.enums.Source;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.SeriesContentSyncService;
import io.github.nikoir.series.tracker.strategy.context.ExternalContentSearchStrategyContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesSyncFacade {
    private final ExternalContentSearchStrategyContext searchStrategyContext;
    private final SeriesContentSyncService contentSyncService;
    private final SeriesFinderFacade seriesFinderFacade;

    @Transactional
    public Series findOrCreateWithSync(Map<ExternalId, String> externalIds) {
        Series series = seriesFinderFacade.findOrCreateSeries(externalIds);
        syncSeriesWithReleases(series);
        return series;
    }

    @Transactional
    public SyncResult syncSeriesWithReleases(Series series) {
        // 1. Маппим внешние id
        Map<ExternalId, String> externalIds = ExternalId.mapExternalIds(series.getExternalIds());

        // 2. Получаем внешние данные
        List<SeasonViewRs> externalSeasons = searchStrategyContext.search(externalIds);
        Source source = searchStrategyContext
                .getExternalContentSearchStrategy()
                .getDataSource();

        // 3. Синхронизируем контент с зависимостями
        SyncResult contentResult = contentSyncService.syncSeriesContent(series, externalSeasons, source);

        if (!contentResult.hasNewContent()) {
            return SyncResult.empty();
        }

        return contentResult;
    }
}