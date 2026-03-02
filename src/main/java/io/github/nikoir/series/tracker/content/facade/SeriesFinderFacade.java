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
class SeriesFinderFacade {
    private final SeriesService seriesService;
    private final SeriesGetStrategyContext getStrategyContext;
    private final DBSeriesDetailAdapter detailAdapter;

    SeriesDetailViewRs findOrCreateSeries(Map<ExternalId, String> externalIds) {
        return seriesService.find(externalIds)
                .map(detailAdapter::toViewDto)
                .orElseGet(() -> createSeries(externalIds));
    }

    SeriesDetailViewRs findSeries(Map<ExternalId, String> externalIds) {
        return seriesService.find(externalIds)
                .map(detailAdapter::toViewDto)
                .orElseGet(() -> getStrategyContext.get(externalIds));
    }

    private SeriesDetailViewRs createSeries(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesInfo = getStrategyContext.get(externalIds);
        Series series = seriesService.create(seriesInfo);
        return detailAdapter.toViewDto(series);
    }
}
