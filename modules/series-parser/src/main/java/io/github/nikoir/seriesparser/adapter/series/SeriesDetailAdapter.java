package io.github.nikoir.seriesparser.adapter.series;

import io.github.nikoir.seriesparser.dto.internal.SeriesDetailViewRs;

public interface SeriesDetailAdapter<T> {
    SeriesDetailViewRs toViewDto(T source);
}
