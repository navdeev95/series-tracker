package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.shorts.DatabaseSeriesShortAdapter;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.request.SeriesSubscriptionRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.service.UserSubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSubscribeFacade {
    private final SeriesSyncFacade syncFacade;
    private final UserSubscriptionService subscriptionService;
    private final DatabaseSeriesShortAdapter seriesShortAdapter;

    @Transactional
    public void subscribe(Long userTelegramId, Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesDto = syncFacade.findOrCreateWithSync(externalIds);

        if (seriesDto.id() != null) {
            subscriptionService.subscribeIfNotExists(userTelegramId, seriesDto.id());
        }
    }

    public void unsubscribe(Long userTelegramId, Map<ExternalId, String> externalIds) {
        subscriptionService.unsubscribeByExternalIds(userTelegramId, externalIds);
    }

    public PagedModel<SeriesListViewRs> getSubscriptionList(SeriesSubscriptionRq request) {
        return seriesShortAdapter.toViewDtoPage(
                subscriptionService.getSubscriptionList(request)
        );
    }
}
