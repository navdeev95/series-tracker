package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.content.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.strategy.context.SeriesGetStrategyContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesFinderFacade {
    private final SeriesService seriesService;
    private final SeriesGetStrategyContext getStrategyContext;
    private final DBSeriesDetailAdapter detailAdapter;

    @Transactional
    Series findOrCreateSeries(Map<ExternalId, String> externalIds) {
        return seriesService
                .find(externalIds)
                .orElseGet(() -> createSeries(externalIds));
    }

    SeriesDetailViewRs findSeries(Map<ExternalId, String> externalIds) {
        Optional<Series> series = seriesService.find(externalIds);
        if (series.isPresent()) {
            return detailAdapter.toViewDto(series.get());
        }
        return getStrategyContext.get(externalIds);
    }

    private Series createSeries(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesInfo = getStrategyContext.get(externalIds);
        return seriesService.create(seriesInfo);
    }
}
