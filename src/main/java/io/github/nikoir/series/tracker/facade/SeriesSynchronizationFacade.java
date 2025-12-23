package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import io.github.nikoir.series.tracker.enums.Source;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.SeriesContentSyncService;
import io.github.nikoir.series.tracker.strategy.context.ExternalContentSearchStrategyContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesSynchronizationFacade {
    private final ExternalContentSearchStrategyContext searchStrategyContext;
    private final SeriesContentSyncService contentSyncService;

    @Transactional
    public SyncResult syncSeriesWithReleases(Series series) {
        // 1. Маппим внешние id
        Map<ExternalId, String> externalIds = mapExternalIds(series.getExternalIds());

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

    private Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIdSeries) {
        Map<ExternalId, String> result = new HashMap<>();
        for (ExternalIdSeries externalId: externalIdSeries) {
            result.put(ExternalId.fromId(externalId.getExternalId().getId()),
                    externalId.getValue());
        }

        return result;
    }
}