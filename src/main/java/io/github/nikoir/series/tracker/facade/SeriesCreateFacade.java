package io.github.nikoir.series.tracker.facade;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.service.SeriesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesCreateFacade {
    private final SeriesService createService;
    private final SeriesSynchronizationFacade synchronizationFacade;

    @Transactional
    public void create(Map<ExternalId, String> externalIds) {
        Series createdSeries = createService.create(externalIds);
        synchronizationFacade.syncSeriesWithReleases(createdSeries);
    }
}
