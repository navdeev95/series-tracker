package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesGetFacade {
    private final SeriesFinderFacade seriesFinderFacade;
    private final UserSubscriptionService subscriptionService;

    public SeriesDetailPersonalizedRs getSeriesInfoForUser(Long userTelegramId,
                                                           Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesInfo = seriesFinderFacade.findSeries(externalIds);
        Long seriesId = seriesInfo.id();
        boolean isUserSubscribed = false;

        if (seriesId != null) {
            isUserSubscribed = subscriptionService.isUserSubscribed(userTelegramId, seriesId);
        }

        return new SeriesDetailPersonalizedRs(seriesInfo, isUserSubscribed);
    }
}
