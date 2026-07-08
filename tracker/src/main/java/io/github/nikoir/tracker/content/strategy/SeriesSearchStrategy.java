package io.github.nikoir.tracker.content.strategy;

import io.github.nikoir.common.dto.request.SeriesSearchRq;
import io.github.nikoir.common.dto.response.SeriesListViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesSearchStrategy extends GettingStrategy {
    PagedModel<SeriesListViewRs> search(SeriesSearchRq request);
}
