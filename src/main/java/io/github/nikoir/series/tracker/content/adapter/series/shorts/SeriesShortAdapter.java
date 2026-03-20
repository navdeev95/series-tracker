package io.github.nikoir.series.tracker.content.adapter.series.shorts;

import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public interface SeriesShortAdapter<T> {
    PagedModel<SeriesListViewRs> toViewDtoPage(T series);

    static PagedModel<SeriesListViewRs> createEmptyPage() {
        return new PagedModel<>(Page.empty());
    }
}
