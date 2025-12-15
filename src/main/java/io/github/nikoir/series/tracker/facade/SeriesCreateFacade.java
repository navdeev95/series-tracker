package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.service.SeriesCreateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesCreateFacade {
    private final SeriesCreateService createService;
    private final SeriesSynchronizationFacade synchronizationFacade;

    @Transactional
    public void create(Map<String, String> externalIds) {
        Series createdSeries = createService.create(externalIds);
        synchronizationFacade.syncSeriesWithReleases(createdSeries);
    }
}
