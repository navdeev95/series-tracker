package io.github.nikoir.seriesparser.adapter;

import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import org.springframework.data.web.PagedModel;

public interface SeriesAdapter<T> {
    PagedModel<SeriesViewRs> toViewDtoPage(T series);
}
