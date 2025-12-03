package io.github.nikoir.seriesparser.service.create.content;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.service.sync.SeriesSynchronizationFacade;
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
