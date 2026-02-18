package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.UserSubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSubscribeFacade {
    private final SeriesSyncFacade seriesSyncFacade;
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
}
