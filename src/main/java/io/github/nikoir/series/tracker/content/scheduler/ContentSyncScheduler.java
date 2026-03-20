package io.github.nikoir.series.tracker.content.scheduler;

import io.github.nikoir.series.tracker.content.facade.SeriesSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ContentSyncScheduler {
    private final SeriesSyncFacade syncFacade;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    @Async
    public void syncContent() {
        syncFacade.syncAndNotifyAllSeries();
    }
}
