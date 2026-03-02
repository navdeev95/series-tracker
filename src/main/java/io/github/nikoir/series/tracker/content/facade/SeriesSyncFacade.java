package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.SeriesContentSyncService;
import io.github.nikoir.series.tracker.content.strategy.context.ExternalContentSearchStrategyContext;
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
    Series findOrCreateWithSync(Map<ExternalId, String> externalIds) {
        Series series = seriesFinderFacade.findOrCreateSeries(externalIds);
        syncSeriesWithReleases(series);
        return series;
    }

    @Transactional
    private SyncResult syncSeriesWithReleases(Series series) {
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