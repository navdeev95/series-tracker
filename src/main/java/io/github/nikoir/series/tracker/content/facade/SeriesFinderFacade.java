package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.content.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.strategy.SeriesGetStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.TMDBExternalIdStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.TMDBSeriesGetStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.WikidataExternalIdStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class SeriesFinderFacade {
    private final SeriesService seriesService;
    private final SeriesGetStrategy seriesGetStrategy;
    private final ExternalIdFacade externalIdFacade;
    private final DBSeriesDetailAdapter detailAdapter;

    public SeriesDetailViewRs findLocallyOrGet(Map<ExternalId, String> externalIds) {
        return findLocally(externalIds)
                .orElseGet(() -> getSeriesAndExternalIds(externalIds));
    }

    private SeriesDetailViewRs getSeriesAndExternalIds(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesDetails = seriesGetStrategy.get(externalIds);

        Map<ExternalId, String> enrichedExternalIds = externalIdFacade.enrichExternalIds(seriesDetails.getExternalIds());
        seriesDetails.setExternalIds(enrichedExternalIds);

        return seriesDetails;
    }

    private Optional<SeriesDetailViewRs> findLocally(Map<ExternalId, String> externalIds) {
        return seriesService.find(externalIds)
                .map(detailAdapter::toViewDto);
    }

}
