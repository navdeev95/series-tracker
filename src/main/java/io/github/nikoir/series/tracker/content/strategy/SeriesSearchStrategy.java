package io.github.nikoir.series.tracker.content.strategy;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesSearchStrategy extends SearchStrategy<SeriesSearchRq, PagedModel<SeriesListViewRs>> {
}
