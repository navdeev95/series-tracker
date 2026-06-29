package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSubscribersRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.common.events.NewContentEvent;
import io.github.nikoir.series.tracker.content.facade.SeriesSubscribeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.web.PagedModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeriesNewContentService {
    private final SeriesSendService seriesSendService;
    private final SeriesSubscribeFacade subscribeFacade;
    private final int BATCH_SIZE = 100;

    @Async
    @EventListener
    public void handleEvent(NewContentEvent newContentEvent) {
        SeriesDetailViewRs seriesDetains = newContentEvent.getSeriesDetails();
        PagedModel<Long> subscribers;
        int page = 0;
        do {
            SeriesSubscribersRq request = new SeriesSubscribersRq(seriesDetains.getInnerId(), page, BATCH_SIZE);
            subscribers = subscribeFacade.getSubscribersTelegramIds(request);
            subscribers.getContent().forEach(userId -> this.handleSubscriber(userId, newContentEvent));
        } while (subscribers.getMetadata().number() + 1 < subscribers.getMetadata().totalPages());
    }

    private void handleSubscriber(Long userId, NewContentEvent newContentEvent) {
        if (newContentEvent.hasNewContent()) {
            seriesSendService.sendNewEpisodeAnswer(userId, newContentEvent);
        }
    }
}
