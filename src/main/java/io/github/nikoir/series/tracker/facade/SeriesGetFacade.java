package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.SeriesService;
import io.github.nikoir.series.tracker.service.UserSubscriptionService;
import io.github.nikoir.series.tracker.strategy.context.SeriesGetStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesGetFacade {
    private final SeriesGetStrategyContext getStrategyContext;
    private final SeriesService seriesService;
    private final UserSubscriptionService subscriptionService;

    public SeriesDetailPersonalizedRs getSeriesInfoForUser(Long userTelegramId,
                                                           Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesInfo = getStrategyContext.get(externalIds);
        Optional<Series> series = seriesService.find(externalIds);

        boolean isUserSubscribed = series.isPresent() &&
                subscriptionService.isUserSubscribed(userTelegramId, series.get().getId());

        return new SeriesDetailPersonalizedRs(seriesInfo,
                series.map(Series::getId).orElse(null),
                isUserSubscribed);
    }
}
