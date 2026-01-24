package io.github.nikoir.series.tracker.adapter.series.shorts;

import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public interface SeriesShortAdapter<T> {
    PagedModel<SeriesShortViewRs> toViewDtoPage(T series);

    static PagedModel<SeriesShortViewRs> createEmptyPage() {
        return new PagedModel<>(Page.empty());
    }
}
