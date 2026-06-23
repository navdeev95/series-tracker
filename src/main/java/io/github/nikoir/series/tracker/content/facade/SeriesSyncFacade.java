package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.event.publisher.NewContentEventPublisher;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.service.SeriesContentSyncService;
import io.github.nikoir.series.tracker.content.strategy.EpisodeSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesSyncFacade {
    private static final int BATCH_SIZE = 1000;

    private final SeriesService seriesService;
    private final DBSeriesDetailAdapter seriesDetailAdapter;
    private final NewContentEventPublisher eventPublisher;

    private final EpisodeSearchStrategy episodeSearchStrategy;
    private final SeriesContentSyncService contentSyncService;


    public void syncAndNotifyAllSeries() {
        int page = 0;
        Page<Series> batch;

        do {
            batch = seriesService.findUncompletedSeries(page, BATCH_SIZE);
            batch.forEach(this::syncAndNotify);
            page++;
        } while (batch.hasNext());
    }

    public void sync(Series series) {
        SeriesDetailViewRs detailViewRs = seriesDetailAdapter.toViewDto(series);
        syncSeriesWithReleases(detailViewRs);
    }

    private void syncAndNotify(Series series) {
        try {
            SeriesDetailViewRs detailViewRs = seriesDetailAdapter.toViewDto(series);
            SyncResult syncResult = syncSeriesWithReleases(detailViewRs);
            if (syncResult.hasNewContent()) {
                eventPublisher.publishEvent(syncResult, detailViewRs);
            }
        } catch (Exception ex) {
            log.error("Failed to sync series ID: {}", series.getId(), ex);
        }
    }

    private SyncResult syncSeriesWithReleases(SeriesDetailViewRs seriesDetails) {
        List<SeasonInfo> externalSeasons = episodeSearchStrategy.searchEpisodes(seriesDetails.getExternalIds());
        Source source = episodeSearchStrategy.getDataSource();

        SyncResult result = contentSyncService.syncSeriesContent(seriesDetails.getInnerId(), externalSeasons, source);
        return result.hasNewContent() ? result : SyncResult.empty();
    }
}