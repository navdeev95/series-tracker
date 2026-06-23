package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSubscribersRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.shorts.DatabaseSeriesShortAdapter;
import io.github.nikoir.series.tracker.common.dto.request.SeriesSubscriptionRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.User;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.SeasonEpisodeService;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.service.UserSubscriptionService;
import io.github.nikoir.series.tracker.content.strategy.SeasonEpisodeGetStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSubscribeFacade {
    private final SeriesSyncFacade syncFacade;
    private final SeriesService seriesService;
    private final UserSubscriptionService subscriptionService;
    private final DatabaseSeriesShortAdapter seriesShortAdapter;
    private final SeasonEpisodeGetStrategy seasonEpisodeGetStrategy;
    private final SeasonEpisodeService seasonEpisodeService;

    @Transactional
    public void subscribeIfNotSubscribed(SeriesDetailViewRs seriesDetailViewRs, Long userTelegramId) {
        Long seriesId;
        if (seriesDetailViewRs.getInnerId() != null) {
            seriesId = seriesDetailViewRs.getInnerId();
        } else {
            Series createdSeries = seriesService.create(seriesDetailViewRs);
            seriesId = createdSeries.getId();

            List<SeasonInfo> seasonsWithEpisodes = seasonEpisodeGetStrategy.getSeasonsWithEpisodes(seriesDetailViewRs.getExternalIds());
            seasonEpisodeService.createSeasonsWithEpisodes(seriesId, seasonsWithEpisodes);
            syncFacade.sync(createdSeries);
        }
        subscriptionService.subscribeIfNotSubscribed(userTelegramId, seriesId);
    }

    public void unsubscribe(Long userTelegramId, Map<ExternalId, String> externalIds) {
        subscriptionService.unsubscribeByExternalIds(userTelegramId, externalIds);
    }

    public PagedModel<SeriesListViewRs> getSubscriptionList(SeriesSubscriptionRq request) {
        return seriesShortAdapter.toViewDtoPage(
                subscriptionService.getSubscriptionList(request));
    }

    public PagedModel<Long> getSubscribersTelegramIds(SeriesSubscribersRq request) {
        Page<User> subscribers = subscriptionService.getSubscribersList(request);
        return new PagedModel<>(subscribers.map(User::getTelegramId));
    }
}
