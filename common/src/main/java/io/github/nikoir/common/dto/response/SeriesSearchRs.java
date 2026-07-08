package io.github.nikoir.common.dto.response;

import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

public record SeriesSearchRs (
        PagedModel<SeriesListViewRs> foundSeries
) {
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(foundSeries.getContent());
    }
}
