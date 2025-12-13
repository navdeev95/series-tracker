package io.github.nikoir.series.tracker.service.sync;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import io.github.nikoir.series.tracker.enums.ExternalSource;
import io.github.nikoir.series.tracker.service.search.content.strategy.ExternalContentSearchStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesSynchronizationFacade {
    private final ExternalContentSearchStrategy contentSearchStrategy;
    private final SeriesContentSyncService contentSyncService;

    @Transactional
    public SyncResult syncSeriesWithReleases(Series series) {
        // 1. Получаем внешние данные
        List<SeasonViewRs> externalSeasons = contentSearchStrategy.search(series.getExternalIds());
        ExternalSource source = contentSearchStrategy.getSource();

        // 2. Синхронизируем контент с зависимостями
        SyncResult contentResult = contentSyncService.syncSeriesContent(series, externalSeasons, source);

        if (!contentResult.hasNewContent()) {
            return SyncResult.empty();
        }

        return contentResult;
    }
}