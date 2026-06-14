package io.github.nikoir.series.tracker.content.facade;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.strategy.impl.TMDBSeriesSearchStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SeriesSearchFacade {
    private final TMDBSeriesSearchStrategy searchStrategy;

    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        if (StringUtils.isEmpty(request.title())) {
            return createEmptyResult(request);
        }
        return searchStrategy.search(request);
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
