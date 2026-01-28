package io.github.nikoir.series.tracker.strategy;

import io.github.nikoir.series.tracker.dto.external.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.external.response.SeriesListViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesSearchStrategy extends SearchStrategy<SeriesSearchRq, PagedModel<SeriesListViewRs>> {
}
