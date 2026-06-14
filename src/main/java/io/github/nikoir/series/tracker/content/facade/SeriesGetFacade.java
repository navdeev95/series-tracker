package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static io.github.nikoir.series.tracker.common.dto.response.SeriesDetailPersonalizedRs.SubscriptionStatus.*;

@Service
@RequiredArgsConstructor
public class SeriesGetFacade {
    private final SeriesFinderFacade seriesFinderFacade;
    private final UserSubscriptionService subscriptionService;

    public SeriesDetailPersonalizedRs getSeriesInfoForUser(Long userTelegramId,
                                                           Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesInfo = seriesFinderFacade.findLocallyOrGet(externalIds);
        SeriesDetailPersonalizedRs.SubscriptionStatus subscriptionStatus = getSubscriptionStatus(userTelegramId, seriesInfo);
        return new SeriesDetailPersonalizedRs(seriesInfo, subscriptionStatus);
    }

    public SeriesDetailPersonalizedRs.SubscriptionStatus getSubscriptionStatus(Long userTelegramId,
                                                                                SeriesDetailViewRs seriesInfo) {
        boolean isUserSubscribed = Optional.ofNullable(seriesInfo.getInnerId())
                .map(id -> subscriptionService.isUserSubscribed(userTelegramId, id))
                .orElse(false);
        if (isUserSubscribed) {
            return SUBSCRIBED;
        }

        if (isAvailableForSubscription(seriesInfo)) {
            return AVAILABLE;
        }

        return NOT_AVAILABLE;
    }

    private boolean isAvailableForSubscription(SeriesDetailViewRs seriesDetailViewRs) {
        return seriesDetailViewRs
                .getExternalIds()
                .containsKey(ExternalId.KINOPOISK);
    }
}
