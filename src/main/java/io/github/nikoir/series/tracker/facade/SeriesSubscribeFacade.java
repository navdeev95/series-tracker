package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.SeriesService;
import io.github.nikoir.series.tracker.service.UserService;
import io.github.nikoir.series.tracker.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesSubscribeFacade {
    private final SeriesGetFacade seriesGetFacade;
    private final SeriesService seriesService;
    private final UserSubscriptionService subscriptionService;
    private final SeriesSynchronizationFacade synchronizationFacade;

    public void subscribe(Long userTelegramId, Map<ExternalId, String> externalIds) {
        Long seriesId;
        SeriesDetailPersonalizedRs seriesInfo = seriesGetFacade.getSeriesInfoForUser(userTelegramId, externalIds);

        if (seriesInfo.seriesId() == null) {
            Series createdSeries = seriesService.create(externalIds);
            synchronizationFacade.syncSeriesWithReleases(createdSeries);
            seriesId = createdSeries.getId();
        } else {
            seriesId = seriesInfo.seriesId();
        }

        subscriptionService.subscribe(userTelegramId, seriesId);
    }
}
