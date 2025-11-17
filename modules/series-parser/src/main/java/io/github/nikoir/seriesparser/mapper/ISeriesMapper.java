package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import org.springframework.data.web.PagedModel;

public interface ISeriesMapper<T> {
    PagedModel<SeriesViewRs> toViewDtoPage(T series);
}
