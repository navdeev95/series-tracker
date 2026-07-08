package io.github.nikoir.tracker.content.facade;


import io.github.nikoir.common.dto.response.ExternalId;
import io.github.nikoir.common.dto.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.tracker.content.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static io.github.nikoir.common.dto.response.SeriesDetailPersonalizedRs.SubscriptionStatus.*;

@Service
@RequiredArgsConstructor
public class SeriesPersonalInfoFacade {
    private final SeriesFinderFacade seriesFinderFacade;
    private final UserSubscriptionService subscriptionService;

    public Optional<SeriesDetailPersonalizedRs> getSeriesInfoForUser(Long userTelegramId,
                                                                     Map<ExternalId, String> externalIds) {
        Optional<SeriesDetailViewRs> seriesInfo = seriesFinderFacade.findLocallyOrGet(externalIds);
        if (seriesInfo.isEmpty()) {
            return Optional.empty();
        }
        SeriesDetailPersonalizedRs.SubscriptionStatus subscriptionStatus = getSubscriptionStatus(userTelegramId, seriesInfo.get());
        return Optional.of(new SeriesDetailPersonalizedRs(seriesInfo.get(), subscriptionStatus));
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
