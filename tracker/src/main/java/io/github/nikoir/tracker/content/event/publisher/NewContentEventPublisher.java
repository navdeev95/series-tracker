package io.github.nikoir.tracker.content.event.publisher;

import io.github.nikoir.common.dto.response.EpisodeReleaseViewRs;
import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.common.events.NewContentEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewContentEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    public void publishEvent(List<EpisodeReleaseViewRs> episodeReleases, SeriesDetailViewRs seriesDetail) {
        NewContentEvent event = new NewContentEvent(this);
        event.setEpisodeReleases(episodeReleases);
        event.setSeriesDetails(seriesDetail);

        applicationEventPublisher.publishEvent(event);
    }
}
