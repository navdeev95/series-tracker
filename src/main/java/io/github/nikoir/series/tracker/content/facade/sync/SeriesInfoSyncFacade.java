package io.github.nikoir.series.tracker.content.facade.sync;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.mapper.SeriesDetailMapper;
import io.github.nikoir.series.tracker.content.strategy.SeriesGetStrategy;
import io.github.nikoir.series.tracker.telegram.command.util.SyncUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesInfoSyncFacade {
    private static final int BATCH_SIZE = 1000;

    private final SeriesRepository seriesRepository;
    private final SeriesGetStrategy seriesGetStrategy;
    private final TransactionTemplate transactionTemplate;
    private final SeriesDetailMapper detailMapper;

    public void syncAllSeriesInfo() {
        int page = 0;
        Boolean hasNext;

        do {
            PageRequest pageRequest = PageRequest.of(page, BATCH_SIZE);

            hasNext = transactionTemplate.execute(status -> {
                Page<Series> batch = seriesRepository.searchSeriesWithCompletedSeasons(pageRequest,
                        SyncUtil.getActiveStatuses());

                batch.forEach(this::syncSeriesInfo);
                return batch.hasNext();
            });

            page++;

        } while (Boolean.TRUE.equals(hasNext));
    }

    private void syncSeriesInfo(Series series) {
        try {
            Map<ExternalId, String> externalIds = ExternalId.mapExternalIds(series.getExternalIds());
            Optional<SeriesDetailViewRs> optionalSeriesInfo = seriesGetStrategy.get(externalIds);
            if (optionalSeriesInfo.isEmpty()) {
                log.error("Series '{}' with id = {} not found",
                        series.getTitle(),
                        series.getId());
                return;
            }
            SeriesDetailViewRs seriesInfo = optionalSeriesInfo.get();
            detailMapper.updateEntity(seriesInfo, series);

            log.debug("Series '{}' with id = {} synced",
                    series.getTitle(),
                    series.getId());
        }
        catch (Exception e) {
            log.error("Series '{}' with id = {} sync error",
                    series.getTitle(),
                    series.getId(),
                    e);
        }
    }
}
