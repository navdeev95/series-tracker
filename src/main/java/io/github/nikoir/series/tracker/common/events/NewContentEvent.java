package io.github.nikoir.series.tracker.common.events;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewContentEvent extends ApplicationEvent {
    private SeriesDetailViewRs seriesDetails;
    private int newSeasonsCnt;
    private int newEpisodesCnt;

    public NewContentEvent(Object source) {
        super(source);
    }
}
