package io.github.nikoir.seriesparser.adapter.series;

import io.github.nikoir.seriesparser.dto.internal.SeriesShortViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesShortAdapter<T> {
    PagedModel<SeriesShortViewRs> toViewDtoPage(T series);
}
