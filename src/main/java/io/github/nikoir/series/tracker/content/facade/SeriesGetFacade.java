package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesGetFacade {
    private final SeriesFinderFacade seriesFinderFacade;
    private final UserSubscriptionService subscriptionService;

    public SeriesDetailPersonalizedRs getSeriesInfoForUser(Long userTelegramId,
                                                           Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesInfo = seriesFinderFacade.findSeries(externalIds);

        boolean isUserSubscribed = Optional.ofNullable(seriesInfo.id())
                .map(id -> subscriptionService.isUserSubscribed(userTelegramId, id))
                .orElse(false);

        return new SeriesDetailPersonalizedRs(seriesInfo, isUserSubscribed);
    }
}
