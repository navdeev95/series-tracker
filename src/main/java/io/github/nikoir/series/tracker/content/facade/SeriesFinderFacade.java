package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.detail.DBSeriesDetailAdapter;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.SeriesService;
import io.github.nikoir.series.tracker.content.strategy.SeriesGetStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.TMDBSeriesSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesFinderFacade {
    private final SeriesService seriesService;
    private final SeriesGetStrategy seriesGetStrategy;
    private final TMDBSeriesSearchStrategy seriesSearchStrategy;
    private final ExternalIdFacade externalIdFacade;
    private final DBSeriesDetailAdapter detailAdapter;

    public Optional<SeriesDetailViewRs> findLocallyOrGet(Map<ExternalId, String> externalIds) {
        return findLocally(externalIds)
                .or(() -> getSeriesAndExternalIds(externalIds));
    }

    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        if (StringUtils.isEmpty(request.title())) {
            return createEmptyResult(request);
        }
        return seriesSearchStrategy.search(request);
    }

    private Optional<SeriesDetailViewRs> getSeriesAndExternalIds(Map<ExternalId, String> externalIds) {
        Optional<SeriesDetailViewRs> optionalSeriesDetails = seriesGetStrategy.get(externalIds);

        if (optionalSeriesDetails.isEmpty()) {
            return Optional.empty();
        }
        SeriesDetailViewRs seriesDetails = optionalSeriesDetails.get();

        Map<ExternalId, String> enrichedExternalIds = externalIdFacade.enrichExternalIds(seriesDetails.getExternalIds());
        seriesDetails.setExternalIds(enrichedExternalIds);

        return Optional.of(seriesDetails);
    }

    private Optional<SeriesDetailViewRs> findLocally(Map<ExternalId, String> externalIds) {
        return seriesService.find(externalIds)
                .map(detailAdapter::toViewDto);
    }

    private PagedModel<SeriesListViewRs> createEmptyResult(SeriesSearchRq request) {
        Page<SeriesListViewRs> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(request.page(), request.limit()),
                0
        );
        return new PagedModel<>(emptyPage);
    }

}
