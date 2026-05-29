package io.github.nikoir.series.tracker.content.event.publisher;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.common.events.NewContentEvent;
import io.github.nikoir.series.tracker.content.dto.internal.SyncResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NewContentEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    public void publishEvent(SyncResult syncResult, SeriesDetailViewRs seriesDetail) {
        NewContentEvent event = new NewContentEvent(this);
        event.setNewEpisodesCnt(syncResult.getNewEpisodesCnt());
        event.setNewSeasonsCnt(syncResult.getNewSeasonsCnt());
        event.setSeriesDetails(seriesDetail);

        applicationEventPublisher.publishEvent(event);
    }
}
