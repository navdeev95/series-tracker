package io.github.nikoir.seriesparser.service.search.series;

import io.github.nikoir.seriesparser.dto.api.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.internal.SeriesShortViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesSearchStrategy {
    PagedModel<SeriesShortViewRs> search(SeriesSearchRq request);
}
