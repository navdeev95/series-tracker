package io.github.nikoir.tracker.content.facade.sync;

import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.common.dto.response.ExternalId;
import io.github.nikoir.tracker.content.mapper.SeriesDetailMapper;
import io.github.nikoir.tracker.content.metric.AppMetricService;
import io.github.nikoir.tracker.content.strategy.SeriesGetStrategy;
import io.github.nikoir.tracker.content.util.ExternalIdUtils;
import io.github.nikoir.tracker.telegram.command.util.SyncUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Map;
import java.util.Optional;

import static io.github.nikoir.tracker.content.metric.SyncMetric.SYNC_INFO;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesInfoSyncFacade {
    private static final int BATCH_SIZE = 1000;

    private final SeriesRepository seriesRepository;
    private final SeriesGetStrategy seriesGetStrategy;
    private final TransactionTemplate transactionTemplate;
    private final SeriesDetailMapper detailMapper;
    private final AppMetricService appMetricService;

    public void syncAllSeriesInfo() {
        int page = 0;
        Boolean hasNext;

        do {
            PageRequest pageRequest = PageRequest.of(page, BATCH_SIZE);

            hasNext = transactionTemplate.execute(status -> {
                Page<Series> batch = seriesRepository.searchSeriesWithCompletedSeasons(pageRequest,
                        SyncUtil.getActiveStatuses());

                batch.forEach(this::syncSeriesInfoSafe);
                return batch.hasNext();
            });

            page++;

        } while (Boolean.TRUE.equals(hasNext));
    }

    private void syncSeriesInfoSafe(Series series) {
        try {
            appMetricService.record(SYNC_INFO, () -> syncSeriesInfo(series));
        }
        catch (Exception e) {
            log.error("Series '{}' with id = {} sync error",
                    series.getTitle(),
                    series.getId(),
                    e);
        }
    }

    private void syncSeriesInfo(Series series) {
        Map<ExternalId, String> externalIds = ExternalIdUtils.mapExternalIds(series.getExternalIds());
        Optional<SeriesDetailViewRs> optionalSeriesInfo = seriesGetStrategy.get(externalIds);
        if (optionalSeriesInfo.isEmpty()) {
            log.error("Series '{}' with id = {} not found",
                    series.getTitle(),
                    series.getId());
            throw new IllegalArgumentException();
        }
        SeriesDetailViewRs seriesInfo = optionalSeriesInfo.get();
        detailMapper.updateEntity(seriesInfo, series);

        log.debug("Series '{}' with id = {} synced",
                series.getTitle(),
                series.getId());
    }
}
