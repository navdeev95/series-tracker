package io.github.nikoir.series.tracker.common.dto.response;

import io.github.nikoir.series.tracker.content.enums.Source;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

public record SeriesSearchRs (
        PagedModel<SeriesListViewRs> foundSeries
) {
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(foundSeries.getContent());
    }
}
