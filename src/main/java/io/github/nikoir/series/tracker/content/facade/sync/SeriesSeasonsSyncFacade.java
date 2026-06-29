package io.github.nikoir.series.tracker.content.facade.sync;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.metric.AppMetricService;
import io.github.nikoir.series.tracker.content.metric.SyncMetric;
import io.github.nikoir.series.tracker.content.service.SeasonEpisodeService;
import io.github.nikoir.series.tracker.content.strategy.SeasonEpisodeGetStrategy;
import io.github.nikoir.series.tracker.telegram.command.util.SyncUtil;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

import static io.github.nikoir.series.tracker.content.metric.SyncMetric.SYNC_SEASONS;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesSeasonsSyncFacade {
    private static final int BATCH_SIZE = 1000;
    private final SeasonEpisodeGetStrategy seasonEpisodeGetStrategy;
    private final SeasonEpisodeService seasonEpisodeService;
    private final SeriesRepository seriesRepository;
    private final TransactionTemplate transactionTemplate;
    private final AppMetricService appMetricService;

    public void syncAllSeriesSeasons() {
        int page = 0;
        Boolean hasNext;

        do {
            PageRequest pageRequest = PageRequest.of(page, BATCH_SIZE);

            hasNext = transactionTemplate.execute(status -> {
                Page<Series> batch = seriesRepository.searchSeriesWithCompletedSeasons(pageRequest,
                        SyncUtil.getActiveStatuses());

                batch.forEach(this::syncSeriesSeasonsSafe);
                return batch.hasNext();
            });

            page++;

        } while (Boolean.TRUE.equals(hasNext));
    }


    public void syncSeriesSeasons(Series series) {
        Map<ExternalId, String> externalIds = ExternalId.mapExternalIds(series.getExternalIds());

        List<SeasonInfo> externalSeasons = seasonEpisodeGetStrategy.getSeasonsWithEpisodes(externalIds);

        List<SeasonInfo> missingSeasons = seasonEpisodeService.findMissingSeasonsWithEpisodes(series.getId(), externalSeasons);
        seasonEpisodeService.createSeasonsWithEpisodes(series.getId(), missingSeasons);
    }

    private void syncSeriesSeasonsSafe(Series series) {
        try {
            appMetricService.record(SYNC_SEASONS, () -> syncSeriesSeasons(series));
        } catch (Exception e) {
            log.error("Failed to sync seasons for series '{}' with id = {}",
                    series.getTitle(),
                    series.getId(),
                    e);
        }
    }

}
