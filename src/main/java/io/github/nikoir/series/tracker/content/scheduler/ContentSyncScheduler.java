package io.github.nikoir.series.tracker.content.scheduler;

import io.github.nikoir.series.tracker.content.facade.SeriesSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentSyncScheduler {
    private final SeriesSyncFacade syncFacade;

    @Scheduled(fixedRate = 60000)
    @Async
    public void syncContent() {
        syncFacade.syncAndNotifyAllSeries();
    }
}
