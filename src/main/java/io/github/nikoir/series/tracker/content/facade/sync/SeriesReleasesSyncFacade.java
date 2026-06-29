package io.github.nikoir.series.tracker.content.facade.sync;

import io.github.nikoir.series.tracker.common.dto.response.EpisodeReleaseViewRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SyncReleaseResult;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.event.publisher.NewContentEventPublisher;
import io.github.nikoir.series.tracker.content.service.SeriesReleaseSyncService;
import io.github.nikoir.series.tracker.content.strategy.EpisodeSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesReleasesSyncFacade {
    private static final int BATCH_SIZE = 1000;

    private final SeriesRepository seriesRepository;

    private final DBSeriesDetailAdapter seriesDetailAdapter;
    private final NewContentEventPublisher eventPublisher;

    private final EpisodeSearchStrategy episodeSearchStrategy;
    private final SeriesReleaseSyncService contentSyncService;


    public void syncAndNotifyAllSeriesReleases() {
        int page = 0;
        Page<Series> batch;

        do {
            PageRequest pageRequest = PageRequest.of(page, BATCH_SIZE);
            batch = seriesRepository.searchSeriesWithoutReleases(pageRequest);
            batch.forEach(this::syncReleasesAndNotify);
            page++;
        } while (batch.hasNext());
    }

    public List<EpisodeReleaseViewRs> syncSeriesReleases(Series series) {
        Map<ExternalId, String> externalIds = ExternalId.mapExternalIds(series.getExternalIds());
        List<SeasonInfo> externalSeasons = episodeSearchStrategy.searchEpisodes(externalIds);
        Source source = episodeSearchStrategy.getDataSource();

        return contentSyncService.syncReleases(series, externalSeasons, source);
    }

    private void syncReleasesAndNotify(Series series) {
        try {
            SeriesDetailViewRs detailViewRs = seriesDetailAdapter.toViewDto(series);
            List<EpisodeReleaseViewRs> syncReleaseResult = syncSeriesReleases(series);
            if (!syncReleaseResult.isEmpty()) {
                eventPublisher.publishEvent(syncReleaseResult, detailViewRs);
            }
        } catch (Exception ex) {
            log.error("Failed to sync series ID: {}", series.getId(), ex);
        }
    }
}