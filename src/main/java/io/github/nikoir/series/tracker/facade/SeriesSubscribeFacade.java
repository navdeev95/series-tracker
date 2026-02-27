package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.SeriesService;
import io.github.nikoir.series.tracker.service.UserSubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSubscribeFacade {
    private final SeriesSyncFacade seriesSyncFacade;
    private final SeriesService seriesService;
    private final UserSubscriptionService subscriptionService;

    @Transactional
    public void subscribe(Long userTelegramId, Map<ExternalId, String> externalIds) {
        try {
            Series series = seriesSyncFacade.findOrCreateWithSync(externalIds);

            if (!subscriptionService.isUserSubscribed(userTelegramId, series.getId())) {
                subscriptionService.subscribe(userTelegramId, series.getId());
            }
        } catch (Exception ex) {
            log.error("Error while subscribe to series: ", ex);
            throw ex;
        }

    }

    public void unsubscribe(Long userTelegramId, Map<ExternalId, String> externalIds) {
        try {
            Optional<Series> series = seriesService.find(externalIds);
            if (series.isEmpty()) {
                throw new IllegalArgumentException("Not found series");
            }
            if (subscriptionService.isUserSubscribed(userTelegramId, series.get().getId())) {
                subscriptionService.unsubscribe(userTelegramId, series.get().getId());
            }
        } catch (Exception ex) {
            log.error("Error while unsubscribe from series: ", ex);
            throw ex;
        }
    }
}
