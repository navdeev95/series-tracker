package io.github.nikoir.series.tracker.common.events;

import io.github.nikoir.series.tracker.common.dto.response.EpisodeReleaseViewRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
public class NewContentEvent extends ApplicationEvent {
    private SeriesDetailViewRs seriesDetails;
    private List<EpisodeReleaseViewRs> episodeReleases;

    public NewContentEvent(Object source) {
        super(source);
    }

    public boolean hasNewContent() {
        return !episodeReleases.isEmpty();
    }
}
