package io.github.nikoir.series.tracker.strategy;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesSearchStrategy {
    PagedModel<SeriesShortViewRs> search(SeriesSearchRq request);
}
