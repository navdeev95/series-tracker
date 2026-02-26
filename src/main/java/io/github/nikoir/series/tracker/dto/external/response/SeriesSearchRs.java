package io.github.nikoir.series.tracker.dto.external.response;

import io.github.nikoir.series.tracker.enums.Source;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

public record SeriesSearchRs (
        PagedModel<SeriesListViewRs> foundSeries,
        Source source
) {
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(foundSeries.getContent());
    }
}
