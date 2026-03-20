package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.event.publisher.NewContentEventPublisher;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.service.sync.SeriesSyncService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesSyncFacade {
    private static final int BATCH_SIZE = 1000;

    private final SeriesService seriesService;
    private final SeriesSyncService syncService;
    private final SeriesFinderFacade finderFacade;
    private final DBSeriesDetailAdapter seriesDetailAdapter;
    private final NewContentEventPublisher eventPublisher;

    public void syncAndNotifyAllSeries() {
        int page = 0;
        Page<Series> batch;

        do {
            batch = seriesService.findUncompletedSeries(page, BATCH_SIZE);
            batch.forEach(this::syncAndNotify);
            page++;
        } while (batch.hasNext());
    }

    @Transactional
    public SeriesDetailViewRs findOrCreateWithSync(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesDto = finderFacade.findOrCreateSeries(externalIds);
        syncService.syncSeriesWithReleases(seriesDto);

        return seriesDto;
    }

    private void syncAndNotify(Series series) {
        try {
            SeriesDetailViewRs detailViewRs = seriesDetailAdapter.toViewDto(series);
            SyncResult syncResult = syncService.syncSeriesWithReleases(detailViewRs);
            if (syncResult.hasNewContent()) {
                eventPublisher.publishEvent(syncResult, detailViewRs);
            }
        } catch (Exception ex) {
            log.error("Failed to sync series ID: {}", series.getId(), ex);
        }
    }
}