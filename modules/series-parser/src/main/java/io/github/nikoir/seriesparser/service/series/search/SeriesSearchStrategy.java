package io.github.nikoir.seriesparser.service.series.search;

import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesSearchStrategy {
    PagedModel<SeriesViewRs> search(SeriesSearchRq request);
}
