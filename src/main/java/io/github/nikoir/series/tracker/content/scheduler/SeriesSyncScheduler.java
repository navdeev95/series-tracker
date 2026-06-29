package io.github.nikoir.series.tracker.content.scheduler;

import io.github.nikoir.series.tracker.content.facade.sync.SeriesInfoSyncFacade;
import io.github.nikoir.series.tracker.content.facade.sync.SeriesReleasesSyncFacade;
import io.github.nikoir.series.tracker.content.facade.sync.SeriesSeasonsSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class SeriesSyncScheduler {
    private final SeriesReleasesSyncFacade releasesSyncFacade;
    private final SeriesInfoSyncFacade infoSyncFacade;
    private final SeriesSeasonsSyncFacade seasonsSyncFacade;

    private final ReentrantLock syncLock = new ReentrantLock(true);

    @Scheduled(cron = "${sync.info.cron:0 0 0 * * *}")
    @Async
    public void syncInfo() {
        syncWithLock(infoSyncFacade::syncAllSeriesInfo);
    }

    @Scheduled(cron = "${sync.seasons.cron:0 0 1 * * *}")
    @Async
    public void syncSeasons() {
        syncWithLock(seasonsSyncFacade::syncAllSeriesSeasons);
    }

    @Scheduled(cron = "${sync.releases.cron:0 0 * * * *}")
    @Async
    public void syncReleases() {
        syncWithLock(releasesSyncFacade::syncAndNotifyAllSeriesReleases);
    }

    private void syncWithLock(Runnable syncTask) {
        syncLock.lock();
        try {
            syncTask.run();
        } finally {
            syncLock.unlock();
        }
    }
}
