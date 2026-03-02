package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.sync.SeriesContentSyncService;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.service.sync.SeriesSyncService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesSyncFacade {
    private static final int BATCH_SIZE = 1000;

    private final SeriesService seriesService;
    private final SeriesSyncService syncService;
    private final SeriesFinderFacade finderFacade;

    public void syncAllSeries() {
        int page = 0;
        Page<Series> batch;

        do {
            batch = seriesService.findUncompletedSeries(page, BATCH_SIZE);
            batch.forEach(syncService::syncSeriesWithReleases);
            page++;
        } while (batch.hasNext());
    }

    @Transactional
    public SeriesDetailViewRs findOrCreateWithSync(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesDto = finderFacade.findOrCreateSeries(externalIds);

        seriesService.find(externalIds)
                .ifPresent(syncService::syncSeriesWithReleases);

        return seriesDto;
    }
}